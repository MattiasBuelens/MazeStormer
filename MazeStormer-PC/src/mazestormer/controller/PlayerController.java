package mazestormer.controller;

import com.google.common.eventbus.Subscribe;

import mazestormer.player.Player;

public class PlayerController extends SubController implements IPlayerController {

	private Player player;
	private MapController map;
	private LogController log;

	public PlayerController(MainController mainController, Player player) {
		super(mainController);
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}

	@Override
	public IMapController map() {
		if (this.map == null) {
			this.map = new MapController(this.getMainController(), getPlayer());
		}
		return this.map;
	}

	@Override
	public ILogController log() {
		if (this.log == null) {
			this.log = new LogController(this.getMainController(), getPlayer());
		}
		return this.log;
	}

	public void terminate() {
		if (this.map != null) {
			this.map.terminate();
		}
		if (this.log != null) {
			this.log.terminate();
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
