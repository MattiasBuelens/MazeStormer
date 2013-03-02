package mazestormer.controller;

import java.util.Collection;

import mazestormer.player.IPlayer;
import mazestormer.player.Player;
import mazestormer.util.EventSource;

public interface IGameController extends EventSource {

	public Collection<IPlayerController> getPlayerControllers();

	public IPlayerController getPersonalPlayerController();

	public IPlayerController getPlayerController(IPlayer player);

}
