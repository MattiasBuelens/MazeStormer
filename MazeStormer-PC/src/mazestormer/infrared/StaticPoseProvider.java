package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class StaticPoseProvider implements PoseProvider {
	
	private Pose pose;

	public StaticPoseProvider(Pose pose) {
		this.pose = pose;
	}

	@Override
	public Pose getPose() {
		return this.pose;
	}

	@Override
	public void setPose(Pose aPose) {
		// disabled
	}
}