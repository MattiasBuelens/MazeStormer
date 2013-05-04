package mazestormer.simulator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lejos.robotics.navigation.Pose;
import mazestormer.geom.GeometryUtils;
import mazestormer.geom.ParallelVisibleRegion;
import mazestormer.infrared.IRSource;
import mazestormer.infrared.OffsettedPoseProvider;
import mazestormer.infrared.OffsettedPoseProvider.Module;
import mazestormer.maze.IMaze;
import mazestormer.maze.PoseTransform;
import mazestormer.player.AbsolutePlayer;
import mazestormer.robot.IRSensor;
import mazestormer.world.Model;
import mazestormer.world.ModelType;
import mazestormer.world.World;

import com.google.common.collect.ImmutableSet;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

public class WorldIRDetector implements IRSensor {

	private float range;

	private OffsettedPoseProvider sensorPoseProvider;
	private final World world;
	private final Class<? extends IRSource> irDetectionType;
	private final IRDetectionMode mode;

	public WorldIRDetector(World world, float range, Class<? extends IRSource> irDetectionType, IRDetectionMode mode) {
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

	private AbsolutePlayer getLocalPlayer() {
		return getWorld().getLocalPlayer();
	}

	private OffsettedPoseProvider getSensorPoseProvider() {
		if (this.sensorPoseProvider == null) {
			// TODO Perhaps use a PoseTransform instead?
			this.sensorPoseProvider = new OffsettedPoseProvider(getLocalPlayer().getRobot().getPoseProvider(),
					Module.IR_SENSOR);
		}
		return this.sensorPoseProvider;
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
		Pose viewPose = getSensorPoseProvider().getPose();
		float viewHeading = viewPose.getHeading();

		// Find rays to visible infrared sources
		List<LineSegment> rays = new ArrayList<LineSegment>();
		for (IRSource irs : getWorld().getAllModels(getIRDetectionType())) {
			// Check if emitting and detected in this mode
			if (irs.isEmitting() && getMode().detects(irs)) {
				// Ignore own robot
				if (irs.equals(getLocalPlayer().getRobot())) {
					continue;
				}
				// Get best detected ray to infrared source
				LineSegment ray = getDetectedRay(obstacles, irs, viewPose);
				if (ray != null) {
					float rayAngle = normalize((float) Math.toDegrees(ray.angle()) - viewHeading);
					if (inRange(rayAngle)) {
						rays.add(ray);
					}
				}
			}
		}

		if (rays.isEmpty()) {
			// No reading
			return Float.NaN;
		} else {
			// Make heading relative to view heading
			float result = normalize((float) getWeightedAngle(rays) - viewHeading);
			// TODO Normalize heading?
			return result;
		}
	}

	private LineSegment getDetectedRay(Geometry obstacles, IRSource subject, Pose viewPose) {
		// Transform to relative maze coordinates
		Point2D viewPoint = getMaze().toRelative(viewPose.getLocation());
		Pose subjectPose = getMaze().toRelative(subject.getPoseProvider().getPose());
		// Transform subject polygon
		Polygon subjectPolygon = GeometryUtils.copy(subject.getEnvelope().getPolygon(), obstacles.getFactory());
		final PoseTransform subjectTransform = new PoseTransform(subjectPose);
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
		Geometry visibleSubject = null;
		try {
			visibleSubject = ParallelVisibleRegion.build(obstacles, subjectPolygon, viewCoord);
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}

		// Exit if invisible
		if (visibleSubject.isEmpty()) {
			return null;
		}

		// Get the ray to the nearest visible point
		Point viewGeomPoint = obstacles.getFactory().createPoint(viewCoord);
		Coordinate[] nearestPoints = DistanceOp.nearestPoints(viewGeomPoint, visibleSubject);
		LineSegment ray = new LineSegment(nearestPoints[0], nearestPoints[1]);

		if (ray.getLength() <= subject.getEnvelope().getDetectionRadius()) {
			// Return viewing ray
			return ray;
		} else {
			// Out of range
			return null;
		}
	}

	private double getWeightedAngle(List<LineSegment> rays) {
		double weightedSum = 0;
		double totalWeight = 0;

		for (LineSegment ray : rays) {
			double weight = 1 / (ray.getLength() + 1);
			double angle = normalize((float) Math.toDegrees(ray.angle()));
			weightedSum += weight * angle;
			totalWeight += weight;
		}

		if (totalWeight > 0) {
			return weightedSum / totalWeight;
		} else {
			return Double.NaN;
		}
	}

	private static float normalize(float angle) {
		while (angle > 180)
			angle -= 360f;
		while (angle < -180)
			angle += 360f;
		return angle;
	}

	public enum IRDetectionMode {

		VIRTUAL(ImmutableSet.of(ModelType.VIRTUAL, ModelType.PHYSICAL)), SEMI_PHYSICAL(ImmutableSet
				.of(ModelType.VIRTUAL));

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
