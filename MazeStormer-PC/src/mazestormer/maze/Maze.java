package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Maze implements IMaze {

	private static final float defaultTileSize = 40f;
	private static final float defaultEdgeSize = 2f;
	// TODO Duplicate constant from BarcodeController, merge?
	private static final float defaultBarLength = 1.85f;

	private final float tileSize;
	private final float edgeSize;
	private final float barLength;

	private Pose origin = new Pose();
	private PoseTransform originTransform = new PoseTransform(origin);

	private Map<LongPoint, Tile> tiles = new HashMap<LongPoint, Tile>();
	private Map<Edge, Line> lines = new HashMap<Edge, Line>();

	private List<MazeListener> listeners = new ArrayList<MazeListener>();

	private final Mesh mesh;
	private Map<Target, Tile> targets = new EnumMap<Target, Tile>(Target.class);
	private Map<Integer, Pose> startPoses = new HashMap<Integer, Pose>();

	public Maze(float tileSize, float edgeSize, float barLength) {
		this.tileSize = tileSize;
		this.edgeSize = edgeSize;
		this.barLength = barLength;
		this.mesh = new Mesh(this);
	}

	public Maze(float tileSize, float edgeSize) {
		this(tileSize, edgeSize, defaultBarLength);
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
	 * Get the length of a bar in this maze.
	 */
	public float getBarLength() {
		return barLength;
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
		this.originTransform = new PoseTransform(origin);

		fireMazeOriginChanged();
	}

	/**
	 * Get the mesh of this maze.
	 */
	public Mesh getMesh() {
		return mesh;
	}

	public int getNumberOfTiles() {
		return tiles.values().size();
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
		}
		return tile;
	}

	private Tile createTile(LongPoint tilePosition) {
		// Create and put tile
		Tile tile = new Tile(tilePosition);
		tiles.put(tilePosition, tile);
		updateMinMax(tile);

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

	public long getMinX() {
		return this.minX;
	}

	public long getMaxX() {
		return this.maxX;
	}

	public long getMinY() {
		return this.minY;
	}

	public long getMaxY() {
		return this.maxY;
	}

	private void updateMinMax(Tile newTile) {
		this.minX = Math.min(getMinX(), newTile.getX());
		this.maxX = Math.max(getMaxX(), newTile.getX());
		this.minY = Math.min(getMinY(), newTile.getY());
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
		LongPoint neighborPosition = direction.shift(tile.getPosition());
		return tiles.get(neighborPosition);
	}

	public Tile getOrCreateNeighbor(Tile tile, Orientation direction) {
		LongPoint neighborPosition = direction.shift(tile.getPosition());
		return getTileAt(neighborPosition);
	}

	/**
	 * Get all tiles on this maze.
	 */
	public Collection<Tile> getTiles() {
		return Collections.unmodifiableCollection(tiles.values());
	}

	/**
	 * Set an edge on this maze.
	 * 
	 * @param tilePosition
	 *            The tile position.
	 * @param orientation
	 *            The edge orientation.
	 * @param type
	 *            The edge type.
	 */
	public void setEdge(LongPoint tilePosition, Orientation orientation, Edge.EdgeType type) {
		Tile tile = getTileAt(tilePosition);
		Edge edge = tile.getEdgeAt(orientation);
		edge.setType(type);

		// Fire edge changed event
		fireEdgeChanged(edge);
		updateEdgeLine(edge);

		// Fire tile changed events
		for (LongPoint touchingPosition : edge.getTouching()) {
			fireTileChanged(getTileAt(touchingPosition));
		}
	}

	/**
	 * Set the barcode of a tile.
	 * 
	 * @param position
	 *            The tile position.
	 * @param barcode
	 *            The barcode.
	 * 
	 * @throws IllegalStateException
	 *             If the tile at the given position does not accept barcodes.
	 */
	public void setBarcode(LongPoint position, Barcode barcode) throws IllegalStateException {
		// Set barcode
		Tile tile = getTileAt(position);
		tile.setBarcode(barcode);

		// Fire tile changed event
		fireTileChanged(tile);
	}

	public void setBarcode(LongPoint position, byte barcode) throws IllegalStateException {
		setBarcode(position, new Barcode(barcode));
	}

	@Override
	public Tile getBarcodeTile(Barcode barcode) {
		checkNotNull(barcode);

		for (Tile tile : tiles.values()) {
			if (tile.getBarcode().equals(barcode)) {
				return tile;
			}
		}

		return null;
	}

	@Override
	public Tile getBarcodeTile(byte barcode) {
		return getBarcodeTile(new Barcode(barcode));
	}

	@Override
	public void setExplored(LongPoint position) {
		Tile tile = getTileAt(position);
		if (!tile.isExplored()) {
			// Set explored
			tile.setExplored();
			// Fire tile explored event
			fireTileExplored(tile);
		}
	}

	/**
	 * Clear this maze, removing all tiles and edges.
	 */
	public void clear() {
		tiles.clear();
		lines.clear();
		targets.clear();
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

	private void fireTileExplored(Tile tile) {
		checkNotNull(tile);
		for (MazeListener listener : listeners) {
			listener.tileExplored(tile);
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
		return originTransform.transform(relativePosition);
	}

	/**
	 * Get the absolute heading in robot coordinates of the given relative
	 * heading in map coordinates.
	 * 
	 * @param relativeHeading
	 *            The relative heading.
	 */
	public float toAbsolute(float relativeHeading) {
		return originTransform.transform(relativeHeading);
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
		return originTransform.transform(relativePose);
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
		return originTransform.inverseTransform(absolutePosition);
	}

	/**
	 * Get the relative heading in map coordinates of the given absolute heading
	 * in robot coordinates.
	 * 
	 * @param absoluteHeading
	 *            The absolute heading.
	 */
	public float toRelative(float absoluteHeading) {
		return originTransform.inverseTransform(absoluteHeading);
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
		return originTransform.inverseTransform(absolutePose);
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
	 * Get a collection of all edges as lines, in relative coordinates.
	 */
	public Collection<Line> getEdgeLines() {
		return Collections.unmodifiableCollection(lines.values());
	}

	/**
	 * Get the line of an edge, in relative coordinates.
	 * 
	 * @param edge
	 *            The edge.
	 */
	public Line getEdgeLine(Edge edge) {
		return createEdgeLine(edge);
	}

	/**
	 * Get the boundaries of an edge, in relative coordinates.
	 * 
	 * @param edge
	 *            The edge.
	 */
	public Rectangle2D getEdgeBounds(Edge edge) {
		// Get edge line
		Line line = getEdgeLine(edge);
		Point p1 = line.getP1(), p2 = line.getP2();

		// Shift points to account for edge size
		float halfLineThickness = getEdgeSize() / 2f;
		Point shift = new Point(halfLineThickness, halfLineThickness);
		p1 = p1.subtract(shift);
		p2 = p2.add(shift);

		// Return bounding box
		return new Rectangle2D.Double(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
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
	 * Get a list of bar rectangles representing the barcode from the given
	 * tile, in tile coordinates relative to the tile position.
	 * 
	 * @param tile
	 *            The tile with barcode.
	 * @return A list of rectangles with an odd number of bars, starting and
	 *         ending with the bounds of a terminating black bar.
	 */
	public List<Rectangle2D> getBarcodeBars(Tile tile) {
		if (!tile.hasBarcode())
			return Collections.emptyList();

		// Barcode direction
		Orientation direction = tile.getShape().getOrientation();
		boolean isVertical = (direction == Orientation.NORTH || direction == Orientation.SOUTH);

		// Center point in tile coordinates
		Point center = isVertical ? new Point(0f, 0.5f) : new Point(0.5f, 0f);
		// Barcode offset in tile coordinates
		float barLength = getBarLength() / getTileSize();
		float offsetAmount = Barcode.getNbBars() / 2 * barLength;
		Point offset = direction.shift(center, -offsetAmount);

		// Create bar rectangles
		List<Rectangle2D> bars = new ArrayList<Rectangle2D>();
		Point barPoint = offset;
		for (int width : tile.getBarcode().getWidths()) {
			// Add bar
			float barWidth = width * barLength;
			if (isVertical) {
				bars.add(new Rectangle2D.Double(barPoint.getX(), barPoint.getY(), 1, barWidth));
			} else {
				bars.add(new Rectangle2D.Double(barPoint.getX(), barPoint.getY(), barWidth, 1));
			}
			// Move to next bar
			barPoint = direction.shift(barPoint, barWidth);
		}
		return bars;
	}

	public Tile getTarget(Target target) {
		return targets.get(target);
	}

	public void setTarget(Target target, Tile tile) {
		checkNotNull(target);
		checkNotNull(tile);
		targets.put(target, tile);
	}

	public Pose getStartPose(int playerNumber) {
		return startPoses.get(playerNumber);
	}

	public void setStartPose(int playerNumber, Pose pose) {
		startPoses.put(playerNumber, checkNotNull(pose));
	}

	public void setStartPose(int playerNumber, LongPoint tilePosition, Orientation orientation) {
		// Center on tile
		Point centerPosition = tilePosition.toPoint().add(new Point(0.5f, 0.5f));
		Point position = fromTile(centerPosition);
		float angle = orientation.getAngle();
		// Create and set pose
		Pose pose = new Pose();
		pose.setLocation(position);
		pose.setHeading(angle);
		// Transform to absolute coordinates
		pose = toAbsolute(pose);
		setStartPose(playerNumber, pose);
	}

	public Tile getSeesawTile(Barcode barcode) {
		for (Tile tile : tiles.values()) {
			if (tile.isSeesaw() && tile.getSeesawBarcode().equals(barcode)) {
				return tile;
			}
		}
		return null;
	}

}
