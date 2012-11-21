package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IBarcodeController extends EventSource{
	
	public void startAction(String action);

	public void stopAction();
	
	public static final String[] ACTIONS = {"Play sound", "Rotate 360 degrees clockwise", "Rotate 360 degrees counter-clockwise",
		"Travel at high speed", "Travel at low speed", "Wait for 5 seconds"};
	
	public void startScan();
	
	public void stopScan();
	
	public void setScanSpeed(double speed);
}
