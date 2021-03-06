package mazestormer.controller;

import mazestormer.barcode.action.ActionType;
import mazestormer.util.EventSource;

public interface IBarcodeController extends EventSource {

	public void startAction(ActionType actionType);

	public void stopAction();

	public void startScan();

	public void stopScan();
	
	public double getScanSpeed();

	public void setScanSpeed(double speed);

	public int getWBThreshold();
	
	public void setWBThreshold(int threshold);
	
	public int getBWThreshold();
	
	public void setBWThreshold(int threshold);
	
	public double getLowSpeed();
	
	public void setLowSpeed(double speed);
	
	public double getHighSpeed();
	
	public void setHighSpeed(double speed);
	
	public double getLowerSpeedBound();
	
	public double getUpperSpeedBound();
}
