package mazestormer.connect;

import static com.google.common.base.Preconditions.checkNotNull;

import mazestormer.maze.Maze;

public class ConnectionContext {

	private String deviceName;
	private Maze loadedMaze;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = checkNotNull(deviceName);
	}

	public Maze getLoadedMaze() {
		return loadedMaze;
	}

	public void setLoadedMaze(Maze loadedMaze) {
		this.loadedMaze = checkNotNull(loadedMaze);
	}

}
