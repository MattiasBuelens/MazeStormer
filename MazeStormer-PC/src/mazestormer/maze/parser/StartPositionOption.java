package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

public class StartPositionOption implements Option {

	private final int playerNumber;
	private final Orientation orientation;

	public StartPositionOption(int playerNumber, Orientation orientation) {
		this.playerNumber = playerNumber;
		this.orientation = orientation;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public void apply(IMaze maze, LongPoint tilePosition, TileToken token) throws ParseException {
		maze.setStartPose(getPlayerNumber(), tilePosition, getOrientation());
	}

	@Override
	public void apply(Tile tile, TileToken token) throws ParseException {
		// Ignore
	}

}
