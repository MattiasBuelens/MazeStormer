package mazestormer.controller;

import java.util.Collection;

import mazestormer.player.IPlayer;
import mazestormer.player.Player;
import mazestormer.util.EventSource;

public interface IGameController extends EventSource {

	public Collection<IPlayerController> getPlayerControllers();

	public IPlayerController getPersonalPlayerController();

	public IPlayerController getPlayerController(IPlayer player);

	public void logToAll(String message);

	public void logTo(String playerID, String message);

	boolean isPersonalPlayer(String playerID);

	public void addPlayer(String playerID);

	public void addPlayer(Player p);

	public void removePlayer(String playerID);

	public void removePlayer(Player p);

	public IPlayer getPlayer(String playerID);

}
