package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.util.AbstractEventSource;
import mazestormer.util.LongPoint;

public class Maze extends AbstractEventSource {

	private static final float defaultTileSize = 40f;
	private static final float defaultEdgeSize = 1f;

	private final float tileSize;
	private final float edgeSize;
	private Pose origin = new Pose();

	private Map<LongPoint, Tile> tiles = new HashMap<LongPoint, Tile>();

	private List<MazeListener> listeners = new ArrayList<MazeListener>();

	public Maze(float tileSize, float edgeSize) {
		this.tileSize = tileSize;
		this.edgeSize = edgeSize;
	}

	public Maze(float tileSize) {
		this(tileSize, defaultEdgeSize);
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
	 * Get the size of an edge in this maze.
	 */
	public float getEdgeSize() {
		return edgeSize;
	}

	/**
	 * Get the pose of the robot at the bottom left corner of the origin tile,
	 * i.e. the tile at {@code (0, 0)}.
	 * 
	 * This pose is used to translate between the absolute pose of the robot and
	 * its relative pose on the maze.
	 */
	public Pose getOrigin() {
		return origin;
	}

	/**
	 * Set the pose of the robot at the bottom left corner of the origin tile.
	 * 
	 * @param origin
	 *            The new origin pose.
	 */
	public void setOrigin(Pose origin) {
		this.origin = origin;
		fireMazeOriginChanged();
	}

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(LongPoint tilePosition) {
		checkNotNull(tilePosition);
		// Try to get tile
		Tile tile = tiles.get(tilePosition);
		if (tile == null) {
			// Create and put tile
			tile = new Tile(tilePosition);
			tiles.put(tilePosition, tile);
			// Fire tile added event
			fireTileAdded(tile);
		}
		return tile;
	}

	/**
	 * Get the tile at the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Tile getTileAt(Point2D tilePosition) {
		checkNotNull(tilePosition);
		return getTileAt(new LongPoint(tilePosition));
	}

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles() {
		return Collections.unmodifiableCollection(tiles.values());
	}

	/**
	 * Add an edge to this maze.
	 * 
	 * @param edge
	 *            The edge to add.
	 * @post The edge is added to the maze tiles at its touching positions.
	 * 			| for each point in edge.getTouching() :
	 * 			|   getTileAt(point).hasEdge(edge)
	 */
	public void addEdge(Edge edge) {
		checkNotNull(edge);

		// Fire edge added event
		fireEdgeAdded(edge);
		updateLines(edge);

		// Add edge to touching tiles
		for (LongPoint touchingPosition : edge.getTouching()) {
			Tile touchingTile = getTileAt(touchingPosition);
			touchingTile.addEdge(edge);
			// Fire tile updated event
			fireTileChanged(touchingTile);
		}
	}

	/**
	 * Clear this maze, removing all tiles and edges.
	 */
	public void clear() {
		tiles.clear();
		fireMazeCleared();
	}

	/**
	 * Add a maze listener.
	 */
	public void addListener(MazeListener listener) {
		checkNotNull(listener);
		listeners.add(listener);
	}

	/**
	 * Remove a maze listener.
	 */
	public void removeListener(MazeListener listener) {
		checkNotNull(listener);
		listeners.remove(listener);
	}

	private void fireTileAdded(Tile tile) {
		checkNotNull(tile);
		for (MazeListener listener : listeners) {
			listener.tileAdded(tile);
		}
	}

	private void fireTileChanged(Tile tile) {
		checkNotNull(tile);
		for (MazeListener listener : listeners) {
			listener.tileChanged(tile);
		}
	}

	private void fireEdgeAdded(Edge edge) {
		checkNotNull(edge);
		for (MazeListener listener : listeners) {
			listener.edgeAdded(edge);
		}
	}

	private void fireMazeOriginChanged() {
		for (MazeListener listener : listeners) {
			listener.mazeOriginChanged(getOrigin());
		}
	}

	private void fireMazeCleared() {
		for (MazeListener listener : listeners) {
			listener.mazeCleared();
		}
	}

	/**
	 * Get the absolute position in robot coordinates of the given relative
	 * position in map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toAbsolute(Point relativePosition) {
		checkNotNull(relativePosition);
		return relativePosition.add(getOrigin().getLocation());
	}

	/**
	 * Get the absolute heading in robot coordinates of the given relative
	 * heading in map coordinates.
	 * 
	 * @param relativeHeading
	 *            The relative heading.
	 */
	public float toAbsolute(float relativeHeading) {
		return normalizeHeading(relativeHeading + getOrigin().getHeading());
	}

	/**
	 * Get the absolute pose in robot coordinates of the given relative pose in
	 * map coordinates.
	 * 
	 * @param relativePose
	 *            The relative pose.
	 */
	public Pose toAbsolute(Pose relativePose) {
		checkNotNull(relativePose);
		Pose pose = new Pose();
		pose.setLocation(toAbsolute(relativePose.getLocation()));
		pose.setHeading(toAbsolute(relativePose.getHeading()));
		return pose;
	}

	/**
	 * Get the relative position in map coordinates of the given absolute
	 * position in robot coordinates.
	 * 
	 * @param absolutePosition
	 *            The absolute position.
	 */
	public Point toRelative(Point absolutePosition) {
		checkNotNull(absolutePosition);
		return absolutePosition.subtract(getOrigin().getLocation());
	}

	/**
	 * Get the relative heading in map coordinates of the given absolute heading
	 * in robot coordinates.
	 * 
	 * @param absoluteHeading
	 *            The absolute heading.
	 */
	public float toRelative(float absoluteHeading) {
		return normalizeHeading(absoluteHeading - getOrigin().getHeading());
	}

	/**
	 * Get the relative pose in map coordinates of the given absolute pose in
	 * robot coordinates.
	 * 
	 * @param absolutePose
	 *            The absolute pose.
	 */
	public Pose toRelative(Pose absolutePose) {
		checkNotNull(absolutePose);
		Pose pose = new Pose();
		pose.setLocation(toRelative(absolutePose.getLocation()));
		pose.setHeading(toRelative(absolutePose.getHeading()));
		return pose;
	}

	/**
	 * Get the position in tile coordinates of the given relative position in
	 * map coordinates.
	 * 
	 * @param relativePosition
	 *            The relative position.
	 */
	public Point toTile(Point relativePosition) {
		double x = relativePosition.getX() / getTileSize();
		double y = relativePosition.getY() / getTileSize();
		return new Point((float) x, (float) y);
	}

	/**
	 * Get the relative position in map coordinates of the bottom left corner of
	 * the given tile position.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 */
	public Point fromTile(Point tilePosition) {
		return tilePosition.multiply(getTileSize());
	}

	/**
	 * Normalize a given heading to ensure it is between -180 and +180 degrees.
	 * 
	 * @param heading
	 *            The heading.
	 */
	private float normalizeHeading(float heading) {
		while (heading < 180)
			heading += 360;
		while (heading > 180)
			heading -= 360;
		return heading;
	}
	
	public Map<Edge, Line> getLines(){
		return Collections.unmodifiableMap(this.lines);
	}
	
	Map<Edge, Line> lines = new HashMap<Edge, Line>();
	
	private void updateLines(Edge edge){
		LongPoint p = edge.getOrientation().shift(edge.getPosition(), getTileSize());
		Line l = new Line(((Double) edge.getPosition().getX()).floatValue(), ((Double) edge.getPosition().getY()).floatValue(), ((Double) p.getX()).floatValue(), ((Double) p.getY()).floatValue());
		this.lines.put(edge, l);	
	}
}
