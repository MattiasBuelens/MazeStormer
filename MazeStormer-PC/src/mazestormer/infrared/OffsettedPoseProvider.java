package mazestormer.infrared;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.PoseTransform;

public class OffsettedPoseProvider implements PoseProvider {

	private final PoseProvider poseProvider;
	private final float radius;
	private final float angleToHeading;

	public OffsettedPoseProvider(PoseProvider relativeTo, Module module) {
		this(relativeTo, module.getRadius(), module.getAngleToHeading());
	}

	public OffsettedPoseProvider(PoseProvider relativeTo, float radius) {
		this(relativeTo, radius, 0);
	}

	public OffsettedPoseProvider(PoseProvider relativeTo, float radius, float angleToHeading) {
		this.poseProvider = relativeTo;
		this.radius = Math.abs(radius);
		this.angleToHeading = angleToHeading;
	}

	public float getRadius() {
		return this.radius;
	}

	public float getAngleToHeading() {
		return this.angleToHeading;
	}

	private PoseProvider getRelativeTo() {
		return this.poseProvider;
	}

	@Override
	public Pose getPose() {
		return transform(getRelativePose());
	}

	@Override
	public void setPose(Pose pose) {
		getRelativeTo().setPose(inverseTransform(pose));
	}

	private Pose getRelativePose() {
		Pose pose = new Pose();
		pose.setLocation(new Point(0, 0).pointAt(getRadius(), getAngleToHeading()));
		pose.setHeading(getAngleToHeading());
		return pose;
	}

	private Pose transform(Pose pose) {
		return new PoseTransform(getRelativeTo().getPose()).transform(pose);
	}

	public Pose inverseTransform(Pose pose) {
		return new PoseTransform(getRelativeTo().getPose()).inverseTransform(pose);
	}

	public enum Module {

		IR_SENSOR(13, 0), RANGE_SCANNER(6, 0), LIGHT_SENSOR(9, 0), ITEM(8, (float) Math.PI), CENTER(0, 0);

		private final float radius;
		private final float angleToHeading;

		private Module(float radius, float angleToHeading) {
			this.radius = Math.abs(radius);
			this.angleToHeading = angleToHeading;
		}

		public float getRadius() {
			return this.radius;
		}

		public float getAngleToHeading() {
			return this.angleToHeading;
		}

	}

}
