package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.Robot;

public class ObservableRobot implements Robot {

	private final PoseProvider poseProvider;
	private final double width;
	private final double height;

	public ObservableRobot(double width, double height) {
		this.poseProvider = new ObservePoseProvider();
		this.width = width;
		this.height = height;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

}
