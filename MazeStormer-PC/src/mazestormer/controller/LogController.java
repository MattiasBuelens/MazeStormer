package mazestormer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public abstract class LogController extends SubController implements ILogController {

	private List<Handler> handlers = new ArrayList<Handler>();

	public LogController(MainController mainController) {
		super(mainController);
	}
	
	protected abstract Logger getLogger();

	@Override
	public void addLogHandler(Handler handler) {
		getLogger().addHandler(handler);
		handlers.add(handler);
	}

	public void terminate() {
		Logger logger = getLogger();
		for (Handler handler : handlers) {
			logger.removeHandler(handler);
		}
	}

}
