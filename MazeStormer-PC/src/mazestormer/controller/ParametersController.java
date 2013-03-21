package mazestormer.controller;

import mazestormer.robot.Pilot;

public class ParametersController extends SubController implements
		IParametersController {

	public ParametersController(MainController mainController) {
		super(mainController);
	}

	private boolean isConnected() {
		return getMainController().isConnected();
	}

	private Pilot getPilot() {
		return getMainController().getControllableRobot().getPilot();
	}

	@Override
	public double getTravelSpeed() {
		return isConnected() ? getPilot().getTravelSpeed() : 0;
	}

	@Override
	public double getMaxTravelSpeed() {
		return isConnected() ? getPilot().getMaxTravelSpeed() : 0;
	}

	@Override
	public void setTravelSpeed(double travelSpeed) {
		if (!isConnected())
			return;

		getPilot().setTravelSpeed(travelSpeed);
		postEvent(new RobotParameterChangeEvent("travelSpeed", travelSpeed));
	}

	@Override
	public double getRotateSpeed() {
		return isConnected() ? getPilot().getRotateSpeed() : 0;
	}

	@Override
	public double getMaxRotateSpeed() {
		return isConnected() ? getPilot().getRotateMaxSpeed() : 0;
	}

	@Override
	public void setRotateSpeed(double rotateSpeed) {
		if (!isConnected())
			return;

		getPilot().setRotateSpeed(rotateSpeed);
		postEvent(new RobotParameterChangeEvent("rotateSpeed", rotateSpeed));
	}

}
