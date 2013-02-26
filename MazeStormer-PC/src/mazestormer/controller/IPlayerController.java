package mazestormer.controller;

import mazestormer.player.Player;
import mazestormer.util.EventSource;

public interface IPlayerController extends EventSource {
	
	public Player getPlayer();
	
	public String getPlayerID();

	public IMapController map();
	
	public ILogController log();
}
