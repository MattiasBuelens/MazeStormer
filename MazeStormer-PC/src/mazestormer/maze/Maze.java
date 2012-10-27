package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import lejos.geom.Point;

public class Maze {

	private Map<Point, Tile> tiles = new HashMap<Point, Tile>();

	/**
	 * Get the tile at the given position.
	 * 
	 * @param position
	 *            The tile position.
	 */
	public Tile getTileAt(Point position) {
		checkNotNull(position);

		Tile tile = tiles.get(position);
		if (tile == null) {
			tile = new Tile(position);
			tiles.put(position, tile);
		}
		return tile;
	}

	/**
	 * Add an edge to this maze.
	 * 
	 * @param edge
	 *            The edge to add.
	 * @post The edge is added to the maze tiles at its touching positions.
	 * 			| for each point in edge.getTouching() :
	 * 			|    getTileAt(point).hasEdge(edge)
	 */
	public void addEdge(Edge edge) {
		checkNotNull(edge);

		// Add edge to touching tiles
		for (Point touchingPosition : edge.getTouching()) {
			Tile touchingTile = getTileAt(touchingPosition);
			touchingTile.addEdge(edge);
		}
	}
}
