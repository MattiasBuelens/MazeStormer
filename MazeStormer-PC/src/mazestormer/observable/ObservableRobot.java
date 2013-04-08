package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.Robot;

public class ObservableRobot implements Robot {

	private PoseProvider poseProvider;

	public ObservableRobot() {
		poseProvider = new ObservePoseProvider();
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

}
