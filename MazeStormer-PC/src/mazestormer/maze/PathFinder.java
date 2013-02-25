package mazestormer.maze;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 * Utility class for path finding.
 */
public class PathFinder {

	protected final Maze maze;

	public PathFinder(Maze maze) {
		this.maze = maze;
	}

	/**
	 * Get the tile at the given absolute position.
	 * 
	 * @param position
	 *            The absolute position.
	 */
	public Tile getTileAt(Point position) {
		// Get tile from absolute position
		Point relativePosition = maze.toRelative(position);
		Point tilePosition = maze.toTile(relativePosition);
		return maze.getTileAt(tilePosition);
	}

	/**
	 * Get the tile at the given absolute pose.
	 * 
	 * @param pose
	 *            The absolute pose.
	 */
	public Tile getTileAt(Pose pose) {
		return getTileAt(pose.getLocation());
	}

	/**
	 * Find the shortest path from the start tile to the goal tile.
	 * 
	 * @param startTile
	 *            The start tile.
	 * @param goalTile
	 *            The goal tile.
	 * @return An ordered list of way points. The way point at the starting tile
	 *         is <strong>not</strong> included.
	 */
	public List<Waypoint> findPath(Tile startTile, Tile goalTile) {
		// Get path of tiles
		Tile[] tiles = maze.getMesh().findTilePath(startTile, goalTile);
		// Get path of way points
		// Note: loop starts at *second* tile
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		for (int i = 1, len = tiles.length; i < len; i++) {
			waypoints.add(toWaypoint(tiles[i]));
		}
		return waypoints;
	}

	/**
	 * Get the way point in absolute coordinates from the given tile.
	 * 
	 * @param tile
	 *            The tile.
	 * @return The way point in absolute coordinates.
	 */
	public Waypoint toWaypoint(Tile tile) {
		// Get center of tile
		Point tilePosition = tile.getPosition().toPoint()
				.add(new Point(0.5f, 0.5f));
		// Get absolute position
		Point absolutePosition = maze.toAbsolute(maze.fromTile(tilePosition));
		// Create way point
		return new Waypoint(absolutePosition.getX(), absolutePosition.getY());
	}

}
