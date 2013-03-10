package mazestormer.controller;

import mazestormer.game.ConnectionMode;
import mazestormer.util.EventSource;

public interface IGameSetUpController extends EventSource {

	public String getPlayerID();

	public void setPlayerID(String playerID);

	public void joinGame(ConnectionMode connectionMode, String gameID);

	public void leaveGame();

	public void setReady(boolean isReady);

	public void startGame();

	public void pauseGame();

	public void stopGame();

}
