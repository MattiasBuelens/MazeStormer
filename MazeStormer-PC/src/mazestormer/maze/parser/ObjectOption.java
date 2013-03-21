package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.maze.Maze;
import mazestormer.util.LongPoint;

public class ObjectOption implements Option {

	@Override
	public void apply(Maze maze, LongPoint tilePosition, TileToken token) throws ParseException {
		// TODO Do we want to mark objects on the maze?
	}

}
