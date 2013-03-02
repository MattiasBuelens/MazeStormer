package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IGameSetUpController extends EventSource {

	public String getPlayerID();

	public void setPlayerID(String playerID);

	public void joinGame(String gameID);

	public void startGame();

	public void leaveGame();

}
