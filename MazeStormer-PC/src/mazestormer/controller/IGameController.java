package mazestormer.controller;

import java.util.Collection;

import mazestormer.game.player.Player;
import mazestormer.game.player.PlayerIdentifier;
import mazestormer.util.EventSource;

public interface IGameController extends EventSource {

	public Collection<IPlayerController> getPlayerControllers();

	public IPlayerController getPersonalPlayerController();

	public IPlayerController getPlayerController(PlayerIdentifier player);

	public void addPlayer(Player player);

	public void removePlayer(Player player);

	public IWorldController getWorldController();

}
