package mazestormer.maze;

import lejos.robotics.navigation.Pose;

public interface MazeListener {

	/*
	 * Tiles
	 */

	/**
	 * Invoked when a tile has been added to the maze.
	 * 
	 * @param tile
	 *            The added tile.
	 */
	void tileAdded(Tile tile);

	/**
	 * Invoked when a tile on the maze has been changed.
	 * 
	 * @param tile
	 *            The changed tile.
	 */
	void tileChanged(Tile tile);

	/*
	 * Edges
	 */

	/**
	 * Invoked when an edge has changed on the maze.
	 * 
	 * @param edge
	 *            The changed edge.
	 */
	void edgeChanged(Edge edge);

	/*
	 * Maze
	 */

	/**
	 * Invoked when the origin of the maze has been changed.
	 * 
	 * @param origin
	 *            The new origin.
	 */
	void mazeOriginChanged(Pose origin);

	/**
	 * Invoked when the maze has been cleared.
	 */
	void mazeCleared();
}
