package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class ObservePoseProvider implements PoseProvider {

	private Pose pose;

	public ObservePoseProvider() {
		setPose(new Pose());
	}

	@Override
	public Pose getPose() {
		return pose;
	}

	@Override
	public void setPose(Pose aPose) {
		this.pose = aPose;
	}

}
