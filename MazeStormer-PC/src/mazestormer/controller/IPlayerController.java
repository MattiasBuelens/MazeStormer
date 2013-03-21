package mazestormer.controller;

import mazestormer.player.IPlayer;
import mazestormer.util.EventSource;

public interface IPlayerController extends EventSource {

	public IPlayer getPlayer();

	public IMapController map();

	public ILogController log();

}
