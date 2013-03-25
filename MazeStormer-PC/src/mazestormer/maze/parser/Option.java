package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.maze.IMaze;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

public interface Option {

	public void apply(IMaze maze, LongPoint tilePosition, TileToken token) throws ParseException;

	public void apply(Tile tile, TileToken token) throws ParseException;

}
