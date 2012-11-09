package mazestormer.controller;

import mazestormer.util.EventSource;

public interface ICalibrationController extends EventSource {

	public int getLowValue();

	public int getHighValue();

	public void setLowValue(int value);

	public void setHighValue(int value);

	public void calibrateLowValue();

	public void calibrateHighValue();

}
