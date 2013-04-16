package mazestormer.simulator;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRSource;
import mazestormer.infrared.OffsettedPoseProvider;
import mazestormer.maze.IMaze;
import mazestormer.robot.IRSensor;
import mazestormer.world.ModelType;
import mazestormer.world.World;

public class WorldIRDetector implements IRSensor {

	private float range;

	private final OffsettedPoseProvider poseProvider;
	private final World world;
	private final IRDetectionMode mode;

	public WorldIRDetector(World world, OffsettedPoseProvider poseProvider, float range, IRDetectionMode mode) {
		this.world = world;
		this.poseProvider = poseProvider;
		this.mode = mode;
		setRange(range);
	}

	public IRDetectionMode getMode() {
		return this.mode;
	}

	private World getWorld() {
		return this.world;
	}

	private OffsettedPoseProvider getPoseProvider() {
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

	@Override
	public float getAngle() {
		return detect();
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}

	private float detect() {
		List<Float> detectedAngles = new ArrayList<Float>();

		for (IRSource irs : getWorld().getAllModelsClass(
				IRSource.class)) {
			Float result = (irs.isEmitting() && getMode().getDetectionSet().contains(irs.getModelType())) ? getDetectedAngle(irs
					.getEnvelope(), irs.getPoseProvider()) : Float.NaN;
			if (!Float.isNaN(result)) {
				detectedAngles.add(result);
			}
		}
		return getBestAngle(detectedAngles.toArray(new Float[0]));
	}

	private static final int DEPTH = 3;

	private Float getDetectedAngle(Envelope envelope, PoseProvider poseProvider) {
		List<Float> detectedAngles = new ArrayList<Float>();
		Pose currentPose = getPoseProvider().getPose();
		Pose centerPose = getPoseProvider().toRelative(currentPose);
		Pose otherPose = poseProvider.getPose();

		// Avoids self-detection
		// Avoids not-detectable joy-riding objects
		if (!((centerPose.getX() == otherPose.getX()) && (centerPose.getY() == otherPose
				.getY()))) {
			Point2D currentPoint = new Point2D.Double(currentPose.getX(),
					currentPose.getY());
			Point2D h_e = new Point2D.Double(
					Math.cos(currentPose.getHeading()), Math.sin(currentPose
							.getHeading()));
			Point2D[] cpsi = envelope.getInternalDiscretization(otherPose, DEPTH);
			Line2D[] edgeLines = getMaze().getAllLines();
			for (int i = 0; i < cpsi.length; i++) {
				Line2D l = new Line2D.Double(currentPoint, cpsi[i]);

				if (l.getP1().distance(l.getP2()) > envelope.getExternalRadius()) {
					continue;
				}

				double angle = getAngle(cpsi[i], currentPoint, h_e);
				if (angle > getRange() || angle < (-1) * getRange()) {
					continue;
				}

				boolean isDetected = true;
				for (int j = 0; j < edgeLines.length && isDetected; j++) {
					isDetected = !l.intersectsLine(edgeLines[j]);
				}
				
				if(isDetected) {
					detectedAngles.add((float) angle);
				}
			}
		}
		
		return getBestAngle(detectedAngles.toArray(new Float[0]));
	}
	
	private float getBestAngle(Float[] angles) {
		if(angles.length != 0) {
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

	private static double getAngle(Point2D p1, Point2D center, Point2D p2) {
		double cp1 = center.distance(p1);
		double cp2 = center.distance(p2);
		double p1p2 = p1.distance(p2);
		return Math.acos((cp1 * cp1 + cp2 * cp2 - p1p2 * p1p2)
				/ (2 * cp1 * cp2));
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

	}
}
