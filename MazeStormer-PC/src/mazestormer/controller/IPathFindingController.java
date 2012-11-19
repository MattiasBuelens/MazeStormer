package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IPathFindingController extends EventSource{
	
	public void startAction(String action);

	public void stopAction();
}
