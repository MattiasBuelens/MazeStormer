package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.maze.IMaze;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

public class ObjectOption implements Option {

	@Override
	public void apply(IMaze maze, LongPoint tilePosition, TileToken token) throws ParseException {
		// TODO Do we want to mark objects on the maze?
	}

	@Override
	public void apply(Tile tile, TileToken token) throws ParseException {
		// Ignore
	}

}
