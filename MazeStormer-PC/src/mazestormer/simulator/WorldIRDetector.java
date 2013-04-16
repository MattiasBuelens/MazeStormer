package mazestormer.simulator;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.geom.VisibleRegion;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRSource;
import mazestormer.maze.IMaze;
import mazestormer.robot.IRSensor;
import mazestormer.world.ModelType;
import mazestormer.world.World;

public class WorldIRDetector implements IRSensor {

	private double radius;
	private float range;

	private final PoseProvider poseProvider;
	private final World world;
	private final IRDetectionMode mode;

	public WorldIRDetector(World world, PoseProvider poseProvider, double radius, float range, IRDetectionMode mode) {
		this.world = world;
		this.poseProvider = poseProvider;
		this.mode = mode;
		setRadius(radius);
		setRange(range);
	}

	public IRDetectionMode getMode() {
		return this.mode;
	}

	private World getWorld() {
		return this.world;
	}

	private PoseProvider getPoseProvider() {
		return this.poseProvider;
	}

	private IMaze getMaze() {
		return getWorld().getMaze();
	}

	private double getRadius() {
		return this.radius;
	}

	private void setRadius(double radius) {
		this.radius = Math.abs(radius);
	}

	private float getRange() {
		return this.range;
	}

	private void setRange(float range) {
		this.range = Math.abs(range);
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
		Float[] angles = getDetectedIRAngles();

		if (angles.length != 0) {
			// Selecting the closest angle difference
			float bestAngle = 180;
			for (int i = 0; i < angles.length; i++) {
				float f = angles[i];
				if (Math.abs(bestAngle) > Math.abs(f)) {
					bestAngle = f;
				}
			}
			return bestAngle;
		}
		return Float.NaN;
	}

	private Float[] getDetectedIRAngles() {
		// TODO: MM add wall for each seesaw

		List<Float> detectedAngles = new ArrayList<Float>();

		for (IRSource irs : getWorld().getAllModelsClass(IRSource.class)) {
			float result = (irs.isEmitting() && getMode().getDetectionSet().contains(irs.getModelType())) ? getDetectedAngle(
					irs.getEnvelope(), irs.getPoseProvider()) : Float.NaN;
			if (!Float.isNaN(result)) {
				detectedAngles.add(result);
			}
		}
		return detectedAngles.toArray(new Float[0]);
	}

	private static final int DEPTH = 3;

	private float getDetectedAngle(Envelope envelope, PoseProvider poseProvider) {
		Pose currentPose = getPoseProvider().getPose();
		Pose otherPose = poseProvider.getPose();

		boolean isDetected = false;
		double angle = 0;
		// Avoids self-detection
		// Avoids not-detectable joy-riding objects
		if (!((currentPose.getX() == otherPose.getX()) && (currentPose.getY() == otherPose.getY()))) {
			Point2D currentPoint = new Point2D.Double(currentPose.getX(), currentPose.getY());
			Point2D otherPoint = new Point2D.Double(otherPose.getX(), otherPose.getY());

			Point2D h_e = new Point2D.Double(Math.cos(currentPose.getHeading()), Math.sin(currentPose.getHeading()));
			Point2D[] cps = envelope.getCombination(currentPose, otherPoint, DEPTH);
			Line2D[] edgeLines = /* getMaze().getEdgeLines().toArray( */new Line2D.Double[0]/* ) */;
			for (int i = 0; i < cps.length && !isDetected; i++) {
				Line2D l = new Line2D.Double(currentPoint, cps[i]);

				if (l.getP1().distance(l.getP2()) > getRadius()) {
					continue;
				}

				angle = getAngle(cps[i], currentPoint, h_e);
				if (angle > getRange() || angle < (-1) * getRange()) {
					continue;
				}

				isDetected = true;
				for (int j = 0; j < edgeLines.length && isDetected; j++) {
					isDetected = !l.intersectsLine(edgeLines[j]);
				}
			}
		}

		// angle to heading axis
		Float result = (isDetected) ? (float) angle : Float.NaN;
		return result;
	}

	private static double getAngle(Point2D p1, Point2D center, Point2D p2) {
		double cp1 = center.distance(p1);
		double cp2 = center.distance(p2);
		double p1p2 = p1.distance(p2);
		return Math.acos((cp1 * cp1 + cp2 * cp2 - p1p2 * p1p2) / (2 * cp1 * cp2));
	}

	// TODO Replace implementation to use this method
	private float getDetectedAngle(Geometry obstacles, Polygon subject, Point viewPoint) {
		// Get the visible part of the subject
		Geometry visibleSubject = VisibleRegion.build(obstacles, subject, viewPoint.getCoordinate());

		// Exit if invisible
		if (visibleSubject.isEmpty()) {
			return Float.NaN;
		}

		// Get the ray to the nearest visible point
		Coordinate[] nearestPoints = DistanceOp.nearestPoints(viewPoint, visibleSubject);
		LineSegment ray = new LineSegment(nearestPoints[0], nearestPoints[1]);

		if (ray.getLength() <= getRadius()) {
			// Return viewing angle
			return (float) ray.angle();
		} else {
			// Out of range
			return Float.NaN;
		}
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

	}
}
