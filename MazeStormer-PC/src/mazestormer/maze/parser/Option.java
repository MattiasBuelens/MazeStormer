package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

public interface Option {

	public void apply(Maze maze, LongPoint tilePosition, TileToken token) throws ParseException;

	public void apply(Tile tile, TileToken token) throws ParseException;

}
