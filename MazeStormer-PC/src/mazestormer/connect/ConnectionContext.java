package mazestormer.connect;

import static com.google.common.base.Preconditions.checkNotNull;

import mazestormer.maze.Maze;

public class ConnectionContext {

	private String deviceName;
	private Maze sourceMaze;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = checkNotNull(deviceName);
	}

	public Maze getSourceMaze() {
		return sourceMaze;
	}

	public void setSourceMaze(Maze sourceMaze) {
		this.sourceMaze = checkNotNull(sourceMaze);
	}

}
