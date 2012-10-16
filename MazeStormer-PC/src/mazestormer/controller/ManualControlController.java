package mazestormer.controller;

import mazestormer.robot.Robot;

public class ManualControlController extends SubController implements
		IManualControlController {

	public ManualControlController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	@Override
	public void moveForward() {
		Robot robot = getRobot();
		robot.forward();
	}

	@Override
	public void moveBackward() {
		Robot robot = getRobot();
		robot.backward();
	}

	@Override
	public void rotateLeft() {
		Robot robot = getRobot();
		robot.rotateLeft();
	}

	@Override
	public void rotateRight() {
		Robot robot = getRobot();
		robot.rotateRight();
	}

	@Override
	public void stop() {
		Robot robot = getRobot();
		robot.stop();
	}

}
