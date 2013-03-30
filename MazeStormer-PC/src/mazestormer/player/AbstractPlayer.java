package mazestormer.player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mazestormer.maze.IMaze;
import mazestormer.robot.Robot;

public abstract class AbstractPlayer implements Player {

	private String playerID;
	private Robot robot;
	private IMaze maze;
	private Logger logger;

	private List<PlayerListener> listeners = new ArrayList<PlayerListener>();

	@Override
	public String getPlayerID() {
		return this.playerID;
	}

	public void setPlayerID(String playerID) {
		String previousID = getPlayerID();
		if (previousID != playerID) {
			this.playerID = playerID;
			for (PlayerListener listener : listeners) {
				listener.playerRenamed(this, previousID, playerID);
			}
		}
	}

	@Override
	public Robot getRobot() {
		return this.robot;
	}

	protected void setRobot(Robot robot) {
		this.robot = robot;
	}

	@Override
	public IMaze getMaze() {
		return this.maze;
	}

	protected void setMaze(IMaze maze) {
		this.maze = maze;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	protected void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void addPlayerListener(PlayerListener listener) {
		listeners.add(listener);
	}

	public void removePlayerListener(PlayerListener listener) {
		listeners.remove(listener);
	}

}
