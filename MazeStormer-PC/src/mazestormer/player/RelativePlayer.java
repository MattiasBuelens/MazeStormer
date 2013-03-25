package mazestormer.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class RelativePlayer implements Player {

	private String playerID;

	private Robot robot;
	private IMaze maze;

	/*
	 * Logging
	 */
	private Logger logger;

	public RelativePlayer() {

	}

	public RelativePlayer(String playerID, Robot robot) {
		setPlayerID(playerID);
		setRobot(robot);
	}

	public RelativePlayer(Robot robot) {
		setRobot(robot);
	}

	@Override
	public String getPlayerID() {
		return this.playerID;
	}

	@Override
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	@Override
	public Robot getRobot() {
		return this.robot;
	}

	public void setRobot(Robot robot) {
		checkNotNull(robot);
		this.robot = robot;
	}

	@Override
	public IMaze getMaze() {
		if (this.maze == null) {
			this.maze = new Maze();
		}
		return this.maze;
	}

	public void setMaze(IMaze maze) {
		checkNotNull(maze);
		this.maze = maze;
	}

	/*
	 * Logging
	 */

	@Override
	public Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(getPlayerID());
			logger.setLevel(Level.ALL);
		}
		return logger;
	}

}
