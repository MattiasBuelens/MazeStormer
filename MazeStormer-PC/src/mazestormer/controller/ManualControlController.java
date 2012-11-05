package mazestormer.controller;

import mazestormer.robot.Pilot;

public class ManualControlController extends SubController implements
		IManualControlController {

	private IScanController scanController;

	public ManualControlController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getRobot().getPilot();
	}

	@Override
	public void moveForward() {
		Pilot pilot = getPilot();
		pilot.forward();
	}

	@Override
	public void moveBackward() {
		Pilot pilot = getPilot();
		pilot.backward();
	}

	@Override
	public void rotateLeft() {
		Pilot pilot = getPilot();
		pilot.rotateLeft();
	}

	@Override
	public void rotateRight() {
		Pilot pilot = getPilot();
		pilot.rotateRight();
	}

	@Override
	public void stop() {
		Pilot pilot = getPilot();
		pilot.stop();
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
