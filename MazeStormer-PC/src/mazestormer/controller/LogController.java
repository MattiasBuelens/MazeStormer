package mazestormer.controller;

import java.util.logging.Handler;

public class LogController extends SubController implements ILogController {

	public LogController(MainController mainController) {
		super(mainController);
	}

	@Override
	public void addLogHandler(Handler handler) {
		getMainController().getLogger().addHandler(handler);
	}

}
