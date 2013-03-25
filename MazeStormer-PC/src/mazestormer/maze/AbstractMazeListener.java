package mazestormer.maze;

import lejos.robotics.navigation.Pose;

public abstract class AbstractMazeListener implements MazeListener {

	@Override
	public void tileAdded(Tile tile) {
	}

	@Override
	public void tileChanged(Tile tile) {
	}

	@Override
	public void tileExplored(Tile tile) {
	}

	@Override
	public void edgeChanged(Edge edge) {
	}

	@Override
	public void mazeOriginChanged(Pose origin) {
	}

	@Override
	public void mazeCleared() {
	}

}
