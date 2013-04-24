package mazestormer.controller;

import mazestormer.game.ConnectionMode;
import mazestormer.util.EventSource;

public interface IGameSetUpController extends EventSource {

	public ConnectionMode getConnectionMode();

	public abstract void setConnectionMode(ConnectionMode connectionMode);

	public String getPlayerID();

	public void setPlayerID(String playerID);

	public String getGameID();

	public abstract void setGameID(String gameID);

	public void joinGame();

	public void leaveGame();

	public void setReady(boolean isReady);

	public void pauseGame();

	public void stopGame();

}
