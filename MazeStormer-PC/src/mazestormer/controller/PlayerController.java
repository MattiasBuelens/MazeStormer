package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import mazestormer.player.Player;

public class PlayerController extends SubController implements IPlayerController {

	private Player player;
	private PlayerMapController map;
	private LogController log;

	public PlayerController(MainController mainController, Player player) {
		super(mainController);
		this.player = player;
		this.map = new PlayerMapController(this.getMainController(), getPlayer());
		this.log = new LogController(this.getMainController(), getPlayer());
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public IPlayerMapController map() {
		return map;
	}

	@Override
	public ILogController log() {
		return log;
	}

	public void terminate() {
		if (map != null) {
			map.terminate();
		}
		if (log != null) {
			log.terminate();
		}
	}

	@Subscribe
	public void onPlayerEvent(PlayerEvent e) {
		switch (e.getEventType()) {
		case PLAYER_REMOVED:
			if (e.getPlayer().equals(getPlayer())) {
				terminate();
			}
		default:
			break;
		}
	}

}
