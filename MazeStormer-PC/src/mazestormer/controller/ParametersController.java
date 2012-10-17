package mazestormer.controller;

import mazestormer.robot.Robot;

public class ParametersController extends SubController implements
		IParametersController {

	public ParametersController(MainController mainController) {
		super(mainController);
	}

	private boolean isConnected() {
		return getMainController().isConnected();
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	@Override
	public double getTravelSpeed() {
		return isConnected() ? getRobot().getTravelSpeed() : 0;
	}

	@Override
	public double getMaxTravelSpeed() {
		return isConnected() ? getRobot().getMaxTravelSpeed() : 0;
	}

	@Override
	public void setTravelSpeed(double travelSpeed) {
		if (!isConnected())
			return;

		getRobot().setTravelSpeed(travelSpeed);
		postEvent(new RobotParameterChangeEvent("travelSpeed", travelSpeed));
	}

	@Override
	public double getRotateSpeed() {
		return isConnected() ? getRobot().getRotateSpeed() : 0;
	}

	@Override
	public double getMaxRotateSpeed() {
		return isConnected() ? getRobot().getRotateMaxSpeed() : 0;
	}

	@Override
	public void setRotateSpeed(double rotateSpeed) {
		if (!isConnected())
			return;

		getRobot().setRotateSpeed(rotateSpeed);
		postEvent(new RobotParameterChangeEvent("rotateSpeed", rotateSpeed));
	}

}
