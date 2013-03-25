package mazestormer.controller;

import mazestormer.player.PlayerIdentifier;
import mazestormer.util.EventSource;

public interface IPlayerController extends EventSource {

	public PlayerIdentifier getPlayer();

	public IMapController map();

	public ILogController log();

}
