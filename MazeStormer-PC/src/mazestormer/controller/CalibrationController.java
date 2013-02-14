package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import mazestormer.connect.ConnectEvent;
import mazestormer.robot.CalibratedLightSensor;

public class CalibrationController extends SubController implements
		ICalibrationController {

	public CalibrationController(MainController mainController) {
		super(mainController);
	}

	private boolean isConnected() {
		return getMainController().isConnected();
	}

	private CalibratedLightSensor getLightSensor() {
		return getMainController().getControllableRobot().getLightSensor();
	}

	@Override
	public int getLowValue() {
		return isConnected() ? getLightSensor().getLow() : 0;
	}

	@Override
	public int getHighValue() {
		return isConnected() ? getLightSensor().getHigh() : 0;
	}

	@Override
	public void setLowValue(int value) {
		if (!isConnected())
			return;

		getLightSensor().setLow(value);
		postEvent(new CalibrationChangeEvent("low", value));
	}

	@Override
	public void setHighValue(int value) {
		if (!isConnected())
			return;

		getLightSensor().setHigh(value);
		postEvent(new CalibrationChangeEvent("high", value));
	}

	@Override
	public void calibrateLowValue() {
		if (!isConnected())
			return;

		getLightSensor().calibrateLow();
		postEvent(new CalibrationChangeEvent("low", getLowValue()));
	}

	@Override
	public void calibrateHighValue() {
		if (!isConnected())
			return;

		getLightSensor().calibrateHigh();
		postEvent(new CalibrationChangeEvent("high", getHighValue()));
	}

	@Subscribe
	public void onConnected(ConnectEvent e) {
		if (e.isConnected()) {
			setHighValue(580);
			setLowValue(360);
		}
	}

}
