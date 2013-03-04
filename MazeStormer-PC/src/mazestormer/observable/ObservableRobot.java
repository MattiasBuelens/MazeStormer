package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.Robot;

public class ObservableRobot implements Robot {

	private PoseProvider poseProvider;

	public ObservableRobot() {
	}

	@Override
	public PoseProvider getPoseProvider() {
		if (poseProvider == null) {
			poseProvider = new ObservePoseProvider();
		}
		return poseProvider;
	}
}
