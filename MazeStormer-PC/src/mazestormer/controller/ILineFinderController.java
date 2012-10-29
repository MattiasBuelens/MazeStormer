package mazestormer.controller;

import mazestormer.util.EventSource;

public interface ILineFinderController extends EventSource {

	public int measureLightValue();
	
	public void startSearching(int highLightValue, int lowLightValue);
	
	public void stopSearching();

}
