package mazestormer.maze;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.maze.path.MazeAStar;

/**
 * Utility class for path finding.
 */
public class PathFinder {

	private final IMaze maze;

	public PathFinder(IMaze iMaze) {
		this.maze = iMaze;
	}

	public final IMaze getMaze() {
		return maze;
	}

	/**
	 * Get the tile at the given absolute position.
	 * 
	 * @param position
	 *            The absolute position.
	 */
	public Tile getTileAt(Point position) {
		// Get tile from absolute position
		Point relativePosition = getMaze().toRelative(position);
		Point tilePosition = getMaze().toTile(relativePosition);
		return getMaze().getTileAt(tilePosition);
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
	 * @return An ordered list of tiles. The starting tile is
	 *         <strong>not</strong> included.
	 */
	public List<Tile> findTilePath(Tile startTile, Tile goalTile) {
		// Get path of tiles
		MazeAStar astar = new MazeAStar(getMaze(), startTile.getPosition(), goalTile.getPosition());
		List<Tile> tiles = astar.findPath();
		// Skip starting tile
		if (tiles == null || tiles.size() <= 1) {
			return new ArrayList<Tile>();
		} else {
			return tiles.subList(1, tiles.size());
		}
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
		List<Tile> tiles = findTilePath(startTile, goalTile);
		// Get path of way points
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		for (Tile tile : tiles) {
			waypoints.add(toWaypoint(tile));
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
		Point relativePosition = getMaze().getTileCenter(tile.getPosition());
		// Get absolute position
		Point absolutePosition = getMaze().toAbsolute(relativePosition);
		// Create way point
		return new Waypoint(absolutePosition.getX(), absolutePosition.getY());
	}

}
