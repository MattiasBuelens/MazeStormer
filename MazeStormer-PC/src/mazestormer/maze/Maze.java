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
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.AbstractEventSource;
import mazestormer.util.LongPoint;

public class Maze extends AbstractEventSource {

	private static final float defaultTileSize = 40f;
	private static final float defaultEdgeSize = 2f;

	private final float tileSize;
	private final float edgeSize;
	private Pose origin = new Pose();

	private Map<LongPoint, Tile> tiles = new HashMap<LongPoint, Tile>();
	private Map<Edge, Line> lines = new HashMap<Edge, Line>();

	private List<MazeListener> listeners = new ArrayList<MazeListener>();

	private Mesh mesh;

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
			// Create tile
			tile = createTile(tilePosition);
			updateMinMax(tile);
		}
		return tile;
	}

	private Tile createTile(LongPoint tilePosition) {
		// Create and put tile
		Tile tile = new Tile(tilePosition);
		tiles.put(tilePosition, tile);

		// Share edges with neighbours
		for (Orientation orientation : Orientation.values()) {
			Tile neighbour = getNeighbor(tile, orientation);
			if (neighbour != null) {
				Edge edge = neighbour.getEdgeAt(orientation.rotateClockwise(2));
				tile.setEdge(edge);
			}
		}

		// Fire tile added event
		fireTileAdded(tile);
		return tile;
	}
	
	private long minX = 0;
	private long maxX = 0;
	private long minY = 0;
	private long maxY = 0;
	
	public long getMinX(){
		return this.minX;
	}
	
	public long getMaxX(){
		return this.maxX;
	}
	
	public long getMinY(){
		return this.minY;
	}
	
	public long getMaxY(){
		return this.maxY;
	}
	
	private void updateMinMax(Tile newTile){
		updateMinX(newTile);
		updateMaxX(newTile);
		updateMinY(newTile);
		updateMaxY(newTile);
	}
	
	
	private void updateMinX(Tile newTile) {
		this.minX = Math.min(getMinX(), newTile.getX());
	}
	
	private void updateMaxX(Tile newTile) {
		this.maxX = Math.max(getMaxX(), newTile.getX());
	}
	
	private void updateMinY(Tile newTile) {
		this.minY = Math.min(getMinY(), newTile.getY());
	}
	
	private void updateMaxY(Tile newTile) {
		this.maxY = Math.max(getMaxY(), newTile.getY());
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

	public Tile getNeighbor(Tile tile, Orientation direction) {
		LongPoint neighborCoordinates = direction.shift(tile.getPosition());
		return tiles.get(neighborCoordinates);
	}

	public Tile getOrCreateNeighbor(Tile tile, Orientation direction) {
		LongPoint neighborCoordinates = direction.shift(tile.getPosition());
		return getTileAt(neighborCoordinates);
	}

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles() {
		return Collections.unmodifiableCollection(tiles.values());
	}

	/**
	 * Get a collection of all edges as lines.
	 */
	public Collection<Line> getLines() {
		return Collections.unmodifiableCollection(lines.values());
	}

	/**
	 * Add an edge to this maze.
	 * 
	 * @param edge
	 *            The edge to add.
	 * @post The edge is added to the maze tiles at its touching positions. |
	 *       for each point in edge.getTouching() : |
	 *       getTileAt(point).hasEdge(edge)
	 */
	public void setEdge(LongPoint position, Orientation direction,
			Edge.EdgeType type) {

		Tile tile = getTileAt(position);
		Edge edge = tile.getEdgeAt(direction);
		edge.setType(type);

		// Fire edge changed event
		fireEdgeChanged(edge);
		updateEdgeLine(edge);

		// Fire tile changed events
		for (LongPoint touchingPosition : edge.getTouching()) {
			fireTileChanged(getTileAt(touchingPosition));
		}
	}

	private void updateEdgeLine(Edge edge) {
		if (edge.getType() == EdgeType.WALL) {
			// Add line
			lines.put(edge, createEdgeLine(edge));
		} else {
			// Remove line
			lines.remove(edge);
		}
	}

	private Line createEdgeLine(Edge edge) {
		LongPoint position = edge.getPosition();
		Orientation orientation = edge.getOrientation();

		// Get edge points in tile coordinates
		Line line = orientation.getLine();
		Point p1 = line.getP1().add(position.toPoint());
		Point p2 = line.getP2().add(position.toPoint());

		// Convert to relative coordinates
		p1 = fromTile(p1);
		p2 = fromTile(p2);

		// Add line
		return new Line(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Clear this maze, removing all tiles and edges.
	 */
	public void clear() {
		tiles.clear();
		lines.clear();
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

	private void fireEdgeChanged(Edge edge) {
		checkNotNull(edge);
		for (MazeListener listener : listeners) {
			listener.edgeChanged(edge);
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

	/**
	 * Returns the mesh of this maze.
	 * 
	 * @param regenerate
	 *            If regenerate is true, the nodes of the mesh of this maze will
	 *            be regenerated. If regenerate is false, the collection of
	 *            nodes of the current mesh stay unchanged and no regeneration
	 *            calculation is executed.
	 * @speed If regenerate is true, the calculation speed is ~O(t*d) with t the
	 *        number of tiles of this maze and with d the average number of open
	 *        directions of a tile. (No storage time is included)
	 * @return The mesh of this maze.
	 */
	public Mesh getMesh(boolean regenerate) {
		if (this.mesh == null) {
			this.mesh = new Mesh(this);
		}
		if (regenerate == true) {
			this.mesh.generateNodes();
		}
		return this.mesh;
	}
}
