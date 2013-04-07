package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

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
	
	private PoseProvider getRelativeTo() {
		return this.poseProvider;
	}
	
	@Override
	public Pose getPose() {
		return transform();
	}

	@Override
	public void setPose(Pose aPose) {
		getRelativeTo().setPose(toRelative(aPose));
	}
	
	private Pose transform() {
		Pose p = getRelativeTo().getPose();
		float h = p.getHeading() + getAngleToHeading();
		float x = p.getX() + (float) (getRadius() * Math.cos(h));
		float y = p.getY() + (float) (getRadius() * Math.sin(h));
		return new Pose(x,y,h);
	}
	
	private Pose toRelative(Pose p) {
		float h = p.getHeading();
		float r_x = p.getX() - (float) (getRadius() * Math.cos(h));
		float r_y = p.getY() - (float) (getRadius() * Math.sin(h));
		float r_h = h - getAngleToHeading();
		return new Pose(r_x,r_y,r_h);
	}
	
	public float getRadius() {
		return this.radius;
	}
	
	public float getAngleToHeading() {
		return this.angleToHeading;
	}
	
	public enum Module {
		// TODO: adding values
		IR_SENSOR(0,0), RANGE_SCANNER(0,0), LIGHT_SENSOR(0,0);
		
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
