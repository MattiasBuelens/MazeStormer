package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.RoundingMode;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.util.AbstractEventSource;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.DoubleMath;

public class Maze extends AbstractEventSource {

	private static final float defaultTileSize = 80f;

	private final float tileSize;
	private Pose origin = new Pose();

	private Table<Long, Long, Tile> tiles = HashBasedTable.create();

	public Maze(float tileSize) {
		this.tileSize = tileSize;
	}

	public Maze() {
		this(defaultTileSize);
	}

	/**
	 * Get the size of a tile in this maze.
	 */
	public float getTileSize() {
		return tileSize;
	}

	/**
	 * Get the pose of the robot at the bottom left corner of the origin tile,
	 * i.e. the tile at {@code (0, 0)}.
	 * 
	 * This pose is used to translate between the absolute pose
	 * of the robot and its relative pose on the maze.
	 */
	public Pose getOrigin() {
		return origin;
	}

	/**
	 * Set the pose of the robot at the bottom left corner of the origin tile.
	 * 
	 * @param origin
	 * 			The new origin pose.
	 */
	public void setOrigin(Pose origin) {
		this.origin = origin;
	}

	/**
	 * Get the tile at the given tile coordinates.
	 * 
	 * @param tileX
	 * 			The X-coordinate of the tile.
	 * @param tileY
	 * 			The Y-coordinate of the tile.
	 */
	public Tile getTileAt(long tileX, long tileY) {
		// Try to get tile
		Tile tile = tiles.get(tileX, tileY);
		if (tile == null) {
			// Create and put tile
			tile = new Tile(tileX, tileY);
			tiles.put(tileX, tileY, tile);
			postEvent(new MazeTileAddEvent(tile));
		}
		return tile;
	}

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(Point tilePosition) {
		checkNotNull(tilePosition);
		// Round towards negative infinity to get bottom left corner
		long tileX = DoubleMath.roundToLong(tilePosition.getX(), RoundingMode.FLOOR);
		long tileY = DoubleMath.roundToLong(tilePosition.getY(), RoundingMode.FLOOR);
		return getTileAt(tileX, tileY);
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

		postEvent(new MazeEdgeAddEvent(edge));
	}

	/**
	 * Get the absolute position in robot coordinates of
	 * the given relative position in map coordinates.
	 * 
	 * @param relativePosition
	 * 			The relative position.
	 */
	public Point toAbsolute(Point relativePosition) {
		checkNotNull(relativePosition);
		return relativePosition.add(getOrigin().getLocation());
	}

	/**
	 * Get the absolute heading in robot coordinates of
	 * the given relative heading in map coordinates.
	 * 
	 * @param relativeHeading
	 * 			The relative heading.
	 */
	public float toAbsolute(float relativeHeading) {
		return normalizeHeading(relativeHeading + getOrigin().getHeading());
	}

	/**
	 * Get the absolute pose in robot coordinates of
	 * the given relative pose in map coordinates.
	 * 
	 * @param relativePose
	 * 			The relative pose.
	 */
	public Pose toAbsolute(Pose relativePose) {
		checkNotNull(relativePose);
		Pose pose = new Pose();
		pose.setLocation(toAbsolute(relativePose.getLocation()));
		pose.setHeading(toAbsolute(relativePose.getHeading()));
		return pose;
	}

	/**
	 * Get the relative position in map coordinates of
	 * the given absolute position in robot coordinates.
	 * 
	 * @param absolutePosition
	 * 			The absolute position.
	 */
	public Point toRelative(Point absolutePosition) {
		checkNotNull(absolutePosition);
		return absolutePosition.subtract(getOrigin().getLocation());
	}

	/**
	 * Get the relative heading in map coordinates of
	 * the given absolute heading in robot coordinates.
	 * 
	 * @param absoluteHeading
	 * 			The absolute heading.
	 */
	public float toRelative(float absoluteHeading) {
		return normalizeHeading(absoluteHeading - getOrigin().getHeading());
	}

	/**
	 * Get the relative pose in map coordinates of
	 * the given absolute pose in robot coordinates.
	 * 
	 * @param absolutePose
	 * 			The absolute pose.
	 */
	public Pose toRelative(Pose absolutePose) {
		checkNotNull(absolutePose);
		Pose pose = new Pose();
		pose.setLocation(toRelative(absolutePose.getLocation()));
		pose.setHeading(toRelative(absolutePose.getHeading()));
		return pose;
	}

	/**
	 * Get the position in tile coordinates of
	 * the given relative position in map coordinates.
	 * 
	 * @param relativePosition
	 * 			The relative position.
	 */
	public Point toTile(Point relativePosition) {
		double x = relativePosition.getX() / getTileSize();
		double y = relativePosition.getY() / getTileSize();
		return new Point((float) x, (float) y);
	}

	/**
	 * Get the relative position in map coordinates of
	 * the bottom left corner of the given tile position.
	 *  
	 * @param tilePosition
	 * 			The tile position.
	 */
	public Point fromTile(Point tilePosition) {
		return tilePosition.multiply(getTileSize());
	}

	/**
	 * Normalize a given heading to ensure it is
	 * between -180 and +180 degrees.
	 * 
	 * @param heading
	 * 			The heading.
	 */
	private float normalizeHeading(float heading) {
		while (heading < 180)
			heading += 360;
		while (heading > 180)
			heading -= 360;
		return heading;
	}
}
