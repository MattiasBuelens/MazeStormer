package mazestormer.player;

import java.util.logging.Logger;

import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public interface Player extends PlayerIdentifier {

	public void setPlayerID(String playerID);

	public Robot getRobot();

	public Maze getMaze();

	public Logger getLogger();

}
