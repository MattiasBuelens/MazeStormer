package mazestormer.controller;

import java.util.List;

import mazestormer.player.Player;
import mazestormer.util.EventSource;

public interface IGameController extends EventSource {
	
	public int getAmountOfPlayerControllers();
	
	public boolean hasAsPlayerController(IPlayerController pc);
	
	public IPlayerController getPlayerControllerAt(int index) 
			throws IndexOutOfBoundsException;
	
	public void addPlayer(Player p);
	
	public void addPlayerController(IPlayerController pc);
	
	public void removePlayerController(IPlayerController pc);

	public List<IPlayerController> getPlayerControllers();

	public IPlayerController getPersonalPlayerController();
}
