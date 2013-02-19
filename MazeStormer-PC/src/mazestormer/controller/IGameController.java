package mazestormer.controller;

import mazestormer.player.Player;
import mazestormer.util.EventSource;

public interface IGameController extends EventSource {
	
	public int getAmountOfPlayerControllers();
	
	public boolean hasAsPlayerController(IPlayerController pc);
	
	public IPlayerController getPersonalPlayerController();
	
	public IPlayerController getPlayerControllerAt(int index) 
			throws IndexOutOfBoundsException;
	
	public void addPlayer(Player p);
	
	public void addPlayerController(IPlayerController pc);
	
	public void removePlayerController(IPlayerController pc);
}
