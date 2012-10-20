package mazestormer.robot;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class DummyPoseProvider implements PoseProvider {

	@Override
	public Pose getPose() {
		return new Pose();
	}

	@Override
	public void setPose(Pose aPose) {

	}

}
