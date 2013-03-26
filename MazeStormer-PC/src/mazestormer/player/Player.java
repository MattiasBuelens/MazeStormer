package mazestormer.player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mazestormer.maze.IMaze;
import mazestormer.robot.Robot;

public abstract class Player implements PlayerIdentifier {

	private String playerID;

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

	public void addPlayerListener(PlayerListener listener) {
		listeners.add(listener);
	}

	public void removePlayerListener(PlayerListener listener) {
		listeners.remove(listener);
	}

	public abstract Robot getRobot();

	public abstract IMaze getMaze();

	public abstract Logger getLogger();

}
