package mazestormer.player;

import java.util.logging.Logger;

import mazestormer.maze.IMaze;
import mazestormer.robot.Robot;

public interface Player extends PlayerIdentifier {

	public void setPlayerID(String playerID);

	public Robot getRobot();

	public IMaze getMaze();

	public Logger getLogger();

	public void addPlayerListener(PlayerListener listener);

	public void removePlayerListener(PlayerListener listener);

}
