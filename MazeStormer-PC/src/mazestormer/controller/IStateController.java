package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IStateController extends EventSource {

	IMainController getMainController();
	
}
