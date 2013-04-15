package mazestormer.player;

import java.util.logging.Logger;

import mazestormer.infrared.IRRobot;
import mazestormer.maze.IMaze;

public interface Player extends PlayerIdentifier {

	public void setPlayerID(String playerID);

	public IRRobot getRobot();

	public IMaze getMaze();

	public Logger getLogger();

	public void addPlayerListener(PlayerListener listener);

	public void removePlayerListener(PlayerListener listener);

}
