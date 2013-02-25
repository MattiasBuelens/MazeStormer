package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IGameSetUpController extends EventSource {
	
	public void createGame(String gameName);
	
	public void joinGame(String gameID);
	
	public String[] refreshLobby();
	
	public void leaveGame();
}
