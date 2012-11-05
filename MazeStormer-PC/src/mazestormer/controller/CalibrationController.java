package mazestormer.controller;

import mazestormer.robot.CalibratedLightSensor;

public class CalibrationController extends SubController implements ICalibrationController  {

	public CalibrationController(MainController mainController) {
		super(mainController);
	}
	
	private boolean isConnected() {
		return getMainController().isConnected();
	}
	
	private CalibratedLightSensor getLightSensor() {
		return getMainController().getRobot().getLightSensor();
	}
	
	@Override
	public int measureLightValue() {
		return getLightSensor().getNormalizedLightValue();
	}

	@Override
	public void setHighValue(int value) {
		if (!isConnected())
			return;

		getLightSensor().setHigh(value);
		postEvent(new CalibrationChangeEvent("high", value));
	}

	@Override
	public void setLowValue(int value) {
		if (!isConnected())
			return;

		getLightSensor().setLow(value);
		postEvent(new CalibrationChangeEvent("low", value));
	}

	@Override
	public int getHighValue() {
		return isConnected() ? getLightSensor().getHigh() : 0;
	}

	@Override
	public int getLowValue() {
		return isConnected() ? getLightSensor().getLow() : 0;
	}

}
