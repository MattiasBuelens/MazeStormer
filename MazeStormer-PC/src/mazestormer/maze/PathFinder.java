package mazestormer.maze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.maze.path.MazeAStar;

import com.google.common.base.Predicate;

/**
 * Utility class for path finding.
 */
public class PathFinder {

	private final IMaze maze;

	public PathFinder(IMaze maze) {
		this.maze = maze;
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
	 *         <strong>not</strong> included. An empty list if there is no path.
	 */
	public List<Tile> findTilePath(Tile startTile, Tile goalTile) {
		return this.findTilePath(startTile, goalTile, null);
	}

	/**
	 * Finds the shortest path, ignores tiles specified by the tileValidator
	 * 
	 * @param startTile
	 *            The start tile.
	 * @param goalTile
	 *            The goal tile.
	 * @param tileValidator
	 *            A validator which should evaluate to true if the given tile is
	 *            valid.
	 * @return An ordered list of tiles. The starting tile is
	 *         <strong>not</strong> included. An empty list if there is no path.
	 */
	public List<Tile> findTilePath(Tile startTile, Tile goalTile, Predicate<Tile> tileValidator) {
		// Get path of tiles
		MazeAStar astar = new MazeAStar(getMaze(), startTile.getPosition(), goalTile.getPosition(), tileValidator);
		List<Tile> tilePath = astar.findPath();
		// Skip starting tile
		if (tilePath == null || tilePath.size() <= 1)
			return new ArrayList<Tile>();
		else
			return tilePath.subList(1, tilePath.size());
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
		return findPath(startTile, goalTile, null);
	}

	/**
	 * Find the shortest path from the start tile to the goal tile.
	 * 
	 * @param startTile
	 *            The start tile.
	 * @param goalTile
	 *            The goal tile.
	 * @param tileValidator
	 *            A validator which should evaluate to true if the given tile is
	 *            valid.
	 * @return An ordered list of way points. The way point at the starting tile
	 *         is <strong>not</strong> included.
	 */
	public List<Waypoint> findPath(Tile startTile, Tile goalTile, Predicate<Tile> tileValidator) {
		List<Tile> tilePath = findTilePath(startTile, goalTile, tileValidator);
		return toWaypointPath(tilePath);
	}

	public List<Tile> findTilePathWithoutSeesaws(Tile startTile, Tile goalTile, final Collection<Seesaw> ignoredSeesaws) {
		return findTilePath(startTile, goalTile, new Predicate<Tile>() {
			@Override
			public boolean apply(Tile tile) {
				return !(tile.isSeesaw() && ignoredSeesaws.contains(tile.getSeesaw()));
			}
		});
	}

	public List<Tile> findTilePathWithoutSeesaws(Tile startTile, Tile goalTile) {
		return findTilePath(startTile, goalTile, new Predicate<Tile>() {
			@Override
			public boolean apply(Tile tile) {
				return !tile.isSeesaw();
			}
		});
	}

	public List<Tile> findTilePathWithoutSeesaw(Tile startTile, Tile goalTile, final Seesaw ignoredSeesaw) {
		return findTilePathWithoutSeesaws(startTile, goalTile, Collections.singleton(ignoredSeesaw));
	}

	public List<Waypoint> toWaypointPath(List<Tile> tilePath) {
		List<Waypoint> waypointPath = new ArrayList<Waypoint>();
		for (Tile tile : tilePath) {
			waypointPath.add(toWaypoint(tile));
		}
		return waypointPath;
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
