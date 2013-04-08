package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IWorldController extends EventSource {

	IWorldMapController map();
	
	ILogController log();

}
