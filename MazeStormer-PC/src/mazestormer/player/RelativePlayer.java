package mazestormer.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class RelativePlayer extends Player {

	private Robot robot;
	private IMaze maze;
	private final Logger logger;

	public RelativePlayer(String playerID, Robot robot) {
		setPlayerID(playerID);
		setRobot(robot);

		logger = Logger.getLogger(getPlayerID());
		logger.setLevel(Level.ALL);
	}

	@Override
	public Robot getRobot() {
		return this.robot;
	}

	public void setRobot(Robot robot) {
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
		this.maze = maze;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

}
