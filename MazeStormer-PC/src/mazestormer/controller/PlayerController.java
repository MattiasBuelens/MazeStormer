package mazestormer.controller;

import mazestormer.player.Player;

public class PlayerController extends SubController implements IPlayerController {
	
	private Player player;
	private IMapController map;
	private ILogController log;
	
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
}
