package mazestormer.maze;

public class MazeTileAddEvent {

	private final Tile tile;

	public MazeTileAddEvent(Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

}
