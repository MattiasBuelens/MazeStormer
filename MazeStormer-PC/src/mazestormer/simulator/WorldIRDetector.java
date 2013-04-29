package mazestormer.simulator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.geom.GeometryUtils;
import mazestormer.geom.VisibleRegion;
import mazestormer.infrared.IRSource;
import mazestormer.infrared.OffsettedPoseProvider;
import mazestormer.infrared.OffsettedPoseProvider.Module;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.robot.IRSensor;
import mazestormer.world.Model;
import mazestormer.world.ModelType;
import mazestormer.world.World;

import com.google.common.collect.ImmutableSet;
import com.google.common.math.DoubleMath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class WorldIRDetector implements IRSensor {

	private float range;

	private OffsettedPoseProvider poseProvider;
	private final World world;
	private final Class<? extends IRSource> irDetectionType;
	private final IRDetectionMode mode;

	private static final double POSE_TOLERANCE = 0.01d;

	public WorldIRDetector(World world, float range,
			Class<? extends IRSource> irDetectionType, IRDetectionMode mode) {
		this.world = world;
		this.irDetectionType = irDetectionType;
		this.mode = mode;
		setRange(range);
	}

	public IRDetectionMode getMode() {
		return this.mode;
	}

	public Class<? extends IRSource> getIRDetectionType() {
		return this.irDetectionType;
	}

	private World getWorld() {
		return this.world;
	}

	private OffsettedPoseProvider getPoseProvider() {
		if (this.poseProvider == null) {
			// TODO Perhaps use a PoseTransform instead?
			this.poseProvider = new OffsettedPoseProvider(getWorld()
					.getLocalPlayer().getRobot().getPoseProvider(),
					Module.IR_SENSOR);
		}
		return this.poseProvider;
	}

	private IMaze getMaze() {
		return getWorld().getMaze();
	}

	private float getRange() {
		return this.range;
	}

	private void setRange(float range) {
		this.range = Math.abs(range);
	}

	private boolean inRange(float heading) {
		return Math.abs(heading) <= getRange();
	}

	@Override
	public float getAngle() {
		return detect();
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}

	private float detect() {
		// Get maze obstacles
		Geometry obstacles = getMaze().getGeometry();
		// Get view pose
		Pose viewPose = getPoseProvider().getPose();
		Point2D viewPoint = viewPose.getLocation();
		float viewHeading = viewPose.getHeading();
		// Get robot pose
		Pose robotPose = getPoseProvider().inverseTransform(viewPose);

		// Find rays to visible infrared sources
		List<LineSegment> rays = new ArrayList<LineSegment>();
		for (IRSource irs : getWorld().getAllModels(getIRDetectionType())) {
			// Check if emitting and detected in this mode
			if (irs.isEmitting() && getMode().detects(irs)) {
				// Ignore own robot
				Pose irsPose = irs.getPoseProvider().getPose();
				if (comparePositions(robotPose.getLocation(),
						irsPose.getLocation())) {
					continue;
				}
				// Get best detected ray to infrared source
				LineSegment ray = getDetectedRay(obstacles, irs, viewPoint);
				float rayAngle = (float) ray.angle() - viewHeading;
				if (ray != null && inRange(rayAngle)) {
					rays.add(ray);
				}
			}
		}

		if (rays.isEmpty()) {
			// No reading
			return Float.NaN;
		} else {
			// Make heading relative to view heading
			float result = getWeightedAngle(rays) - viewHeading;
			// TODO Normalize heading?
			return result;
		}
	}

	private boolean comparePositions(Point2D leftPosition, Point2D rightPosition) {
		return DoubleMath.fuzzyEquals(rightPosition.getX(),
				leftPosition.getX(), POSE_TOLERANCE)
				&& DoubleMath.fuzzyEquals(rightPosition.getY(),
						leftPosition.getY(), POSE_TOLERANCE);
	}

	private static LineSegment getDetectedRay(Geometry obstacles,
			IRSource subject, Point2D viewPoint) {
		// Transform subject polygon
		Polygon subjectPolygon = GeometryUtils.copy(subject.getEnvelope()
				.getPolygon(), obstacles.getFactory());
		final PoseTransform subjectTransform = new PoseTransform(subject
				.getPoseProvider().getPose());
		subjectPolygon.apply(new CoordinateFilter() {
			@Override
			public void filter(Coordinate coord) {
				Point2D point = GeometryUtils.fromCoordinate(coord);
				point = subjectTransform.transform(point);
				coord.setCoordinate(GeometryUtils.toCoordinate(point));
			}
		});
		// Update polygon
		subjectPolygon.geometryChanged();

		// Get the visible part of the subject
		Coordinate viewCoord = GeometryUtils.toCoordinate(viewPoint);
		Geometry visibleSubject = VisibleRegion.build(obstacles,
				subjectPolygon, viewCoord);

		// Exit if invisible
		if (visibleSubject.isEmpty()) {
			return null;
		}

		// Get the ray to the nearest visible point
		Point viewGeomPoint = obstacles.getFactory().createPoint(viewCoord);
		Coordinate[] nearestPoints = DistanceOp.nearestPoints(viewGeomPoint,
				visibleSubject);
		LineSegment ray = new LineSegment(nearestPoints[0], nearestPoints[1]);

		if (ray.getLength() <= subject.getEnvelope().getDetectionRadius()) {
			// Return viewing ray
			return ray;
		} else {
			// Out of range
			return null;
		}
	}

	private float getWeightedAngle(List<LineSegment> rays) {
		double weightedSum = 0;
		double totalWeight = 0;

		for (LineSegment ray : rays) {
			double weight = 1 / (ray.getLength() + 1);
			double angle = ray.angle();
			weightedSum += weight * angle;
			totalWeight += weight;
		}

		if (totalWeight > 0) {
			return (float) (weightedSum / totalWeight);
		} else {
			return Float.NaN;
		}
	}

	public enum IRDetectionMode {

		VIRTUAL(ImmutableSet.of(ModelType.VIRTUAL, ModelType.PHYSICAL)), SEMI_PHYSICAL(
				ImmutableSet.of(ModelType.VIRTUAL));

		private Set<ModelType> detectionSet;

		private IRDetectionMode(Set<ModelType> detectionSet) {
			this.detectionSet = detectionSet;
		}

		public Set<ModelType> getDetectionSet() {
			return this.detectionSet;
		}

		public boolean detects(ModelType type) {
			return getDetectionSet().contains(type);
		}

		public boolean detects(Model model) {
			return detects(model.getModelType());
		}

	}

}
