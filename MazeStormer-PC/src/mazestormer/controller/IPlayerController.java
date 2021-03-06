package mazestormer.controller;

import mazestormer.player.PlayerIdentifier;
import mazestormer.util.EventSource;

public interface IPlayerController extends EventSource {

	public PlayerIdentifier getPlayer();

	public IPlayerMapController map();

	public ILogController log();

}
