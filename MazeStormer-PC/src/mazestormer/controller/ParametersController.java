package mazestormer.controller;

import mazestormer.robot.Robot;

public class ParametersController extends SubController implements
		IParametersController {

	public ParametersController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	@Override
	public double getTravelSpeed() {
		return getRobot().getTravelSpeed();
	}

	@Override
	public double getMaxTravelSpeed() {
		return getRobot().getMaxTravelSpeed();
	}

	@Override
	public void setTravelSpeed(double travelSpeed) {
		getRobot().setTravelSpeed(travelSpeed);
		getEventBus().post(
				new RobotParameterChangeEvent("travelSpeed", travelSpeed));
	}

	@Override
	public double getRotateSpeed() {
		return getRobot().getRotateSpeed();
	}

	@Override
	public double getMaxRotateSpeed() {
		return getRobot().getRotateMaxSpeed();
	}

	@Override
	public void setRotateSpeed(double rotateSpeed) {
		getRobot().setRotateSpeed(rotateSpeed);
		getEventBus().post(
				new RobotParameterChangeEvent("rotateSpeed", rotateSpeed));

	}

}
