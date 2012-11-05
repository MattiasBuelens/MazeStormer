package mazestormer.controller;

import mazestormer.util.EventSource;

public interface ICalibrationController extends EventSource {

	public int measureLightValue();

	public void setHighValue(int value);

	public void setLowValue(int value);

	public int getHighValue();
	
	public int getLowValue();

}
