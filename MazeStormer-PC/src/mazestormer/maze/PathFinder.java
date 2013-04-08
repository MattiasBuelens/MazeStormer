package mazestormer.maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 * Utility class for path finding.
 */
public class PathFinder {

	protected final IMaze maze;

	public PathFinder(IMaze iMaze) {
		this.maze = iMaze;
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
		List<Tile> tiles = maze.getMesh().findTilePath(startTile, goalTile);
		// Get path of way points
		// Note: iteration starts at *second* tile (index 1)
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		if (tiles.size() > 0) {
			Iterator<Tile> it = tiles.listIterator(1);
			while (it.hasNext()) {
				waypoints.add(toWaypoint(it.next()));
			}
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
		Point relativePosition = maze.getTileCenter(tile.getPosition());
		// Get absolute position
		Point absolutePosition = maze.toAbsolute(relativePosition);
		// Create way point
		return new Waypoint(absolutePosition.getX(), absolutePosition.getY());
	}

}
