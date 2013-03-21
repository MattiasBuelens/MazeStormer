package mazestormer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import mazestormer.player.Player;

public class LogController extends SubController implements ILogController {

	private Player player;
	private List<Handler> handlers = new ArrayList<Handler>();

	public LogController(MainController mainController, Player player) {
		super(mainController);
		this.player = player;
	}

	private Player getPlayer() {
		return this.player;
	}

	@Override
	public void addLogHandler(Handler handler) {
		getPlayer().getLogger().addHandler(handler);
		handlers.add(handler);
	}

	public void terminate() {
		Logger logger = getPlayer().getLogger();
		for (Handler handler : handlers) {
			logger.removeHandler(handler);
		}
	}

}
