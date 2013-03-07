package mazestormer.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class Player implements IPlayer {

	private String playerID;

	private Robot robot;
	private Maze maze;

	/*
	 * Logging
	 */
	private Logger logger;

	public Player() {

	}

	public Player(String playerID, Robot robot) {
		setPlayerID(playerID);
		setRobot(robot);
	}

	public Player(Robot robot) {
		setRobot(robot);
	}

	@Override
	public String getPlayerID() {
		return this.playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public Robot getRobot() {
		return this.robot;
	}

	public void setRobot(Robot robot) {
		checkNotNull(robot);
		this.robot = robot;
	}

	public Maze getMaze() {
		if (this.maze == null) {
			this.maze = new Maze();
		}
		return this.maze;
	}

	public void setMaze(Maze maze) {
		checkNotNull(maze);
		this.maze = maze;
	}

	/*
	 * Logging
	 */

	public Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(getPlayerID());
			logger.setLevel(Level.ALL);
		}
		return logger;
	}
}
