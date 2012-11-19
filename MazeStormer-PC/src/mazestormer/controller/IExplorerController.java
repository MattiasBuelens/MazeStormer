package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IExplorerController  extends EventSource{

	public void startExploring();

	public void stopExploring();
	
}
