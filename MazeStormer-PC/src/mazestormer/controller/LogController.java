package mazestormer.controller;

import java.util.logging.Handler;

import mazestormer.player.Player;

public class LogController extends SubController implements ILogController {

	private Player player;

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
	}

}
