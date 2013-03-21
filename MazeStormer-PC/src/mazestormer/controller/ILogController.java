package mazestormer.controller;

import java.util.logging.Handler;

import mazestormer.util.EventSource;

public interface ILogController extends EventSource {

	public void addLogHandler(Handler handler);
	
}
