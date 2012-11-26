package mazestormer.controller;

import mazestormer.barcode.ActionType;
import mazestormer.util.EventSource;

public interface IBarcodeController extends EventSource {

	public void startAction(ActionType actionType);

	public void stopAction();

	public void startScan();

	public void stopScan();
	
	public double getScanSpeed();

	public void setScanSpeed(double speed);
}
