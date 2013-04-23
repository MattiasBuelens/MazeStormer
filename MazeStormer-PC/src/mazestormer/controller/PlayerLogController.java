package mazestormer.controller;

import java.util.logging.Logger;

import mazestormer.game.player.Player;

public class PlayerLogController extends LogController {

	private Player player;

	public PlayerLogController(MainController mainController, Player player) {
		super(mainController);
		this.player = player;
	}

	private Player getPlayer() {
		return this.player;
	}

	@Override
	protected Logger getLogger() {
		return getPlayer().getLogger();
	}

}
