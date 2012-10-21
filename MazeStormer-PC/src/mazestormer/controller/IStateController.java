package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IStateController extends EventSource {

	IMainController getMainController();
	
	public float getXPosition();
	public float getYPosition();
	public float getHeading();

	
}
