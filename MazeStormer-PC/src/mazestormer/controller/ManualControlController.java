package mazestormer.controller;

import mazestormer.robot.Pilot;

public class ManualControlController extends SubController implements
		IManualControlController {

	private IScanController scanController;

	public ManualControlController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getControllableRobot().getPilot();
	}

	@Override
	public void moveForward() {
		getPilot().forward();
	}

	@Override
	public void moveBackward() {
		getPilot().backward();
	}

	@Override
	public void rotateLeft() {
		getPilot().rotateLeft();
	}

	@Override
	public void rotateRight() {
		getPilot().rotateRight();
	}

	@Override
	public void travel(float distance) {
		getPilot().travel(distance, true);
	}

	@Override
	public void rotate(float angle) {
		getPilot().rotate(angle, true);
	}

	@Override
	public void stop() {
		getPilot().stop();
	}

	@Override
	public IParametersController parameters() {
		return getMainController().parameters();
	}

	@Override
	public IScanController scan() {
		if (scanController == null) {
			scanController = new ScanController(getMainController());
		}
		return scanController;
	}

}
