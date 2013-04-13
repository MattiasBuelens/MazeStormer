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

import com.vividsolutions.jts.geom.Geometry;

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

	private Pose origin;
	private PoseTransform originTransform;

	private Map<LongPoint, Tile> tiles = new HashMap<LongPoint, Tile>();

	private List<MazeListener> listeners = new ArrayList<MazeListener>();

	private Map<Target, LongPoint> targets = new EnumMap<Target, LongPoint>(Target.class);
	private Map<Integer, Pose> startPoses = new HashMap<Integer, Pose>();

	private final Map<Barcode, Seesaw> seesaws = new HashMap<>();

	private final EdgeGeometry edgeGeometry;

	public Maze(float tileSize, float edgeSize, float barLength) {
		this.tileSize = tileSize;
		this.edgeSize = edgeSize;
		this.barLength = barLength;

		this.edgeGeometry = new EdgeGeometry(this);
		addListener(edgeGeometry);

		setOriginToDefault();
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

	@Override
	public final float getTileSize() {
		return tileSize;
	}

	@Override
	public final float getEdgeSize() {
		return edgeSize;
	}

	@Override
	public final float getBarLength() {
		return barLength;
	}

	@Override
	public final Pose getOrigin() {
		return origin;
	}

	@Override
	public final Pose getDefaultOrigin() {
		Pose origin = new Pose();
		origin.setLocation(getTileCenter(new LongPoint(0, 0)).reverse());
		origin.setHeading(0f);
		return origin;
	}

	@Override
	public final void setOrigin(Pose origin) {
		this.origin = origin;
		this.originTransform = new PoseTransform(origin);

		fireMazeOriginChanged();
	}

	@Override
	public final void setOriginToDefault() {
		setOrigin(getDefaultOrigin());
	}

	private long minX = 0;
	private long maxX = 0;
	private long minY = 0;
	private long maxY = 0;

	@Override
	public final long getMinX() {
		return this.minX;
	}

	@Override
	public final long getMaxX() {
		return this.maxX;
	}

	@Override
	public final long getMinY() {
		return this.minY;
	}

	@Override
	public final long getMaxY() {
		return this.maxY;
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

	private void updateMinMax(Tile newTile) {
		this.minX = Math.min(getMinX(), newTile.getX());
		this.maxX = Math.max(getMaxX(), newTile.getX());
		this.minY = Math.min(getMinY(), newTile.getY());
		this.maxY = Math.max(getMaxY(), newTile.getY());
	}

	@Override
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

	@Override
	public Tile getTileAt(Point2D tilePosition) {
		checkNotNull(tilePosition);
		return getTileAt(new LongPoint(tilePosition));
	}

	@Override
	public Tile getNeighbor(Tile tile, Orientation direction) {
		LongPoint neighborPosition = direction.shift(tile.getPosition());
		return tiles.get(neighborPosition);
	}

	@Override
	public Tile getOrCreateNeighbor(Tile tile, Orientation direction) {
		LongPoint neighborPosition = direction.shift(tile.getPosition());
		return getTileAt(neighborPosition);
	}

	@Override
	public Collection<Tile> getTiles() {
		return Collections.unmodifiableCollection(tiles.values());
	}

	@Override
	public int getNumberOfTiles() {
		return getTiles().size();
	}

	@Override
	public Collection<Tile> getExploredTiles() {
		List<Tile> exploredTiles = new ArrayList<Tile>();
		for (Tile tile : getTiles()) {
			if (tile.isExplored()) {
				exploredTiles.add(tile);
			}
		}
		return exploredTiles;
	}

	@Override
	public void importTile(Tile tile) {
		importTile(tile, TileTransform.getIdentity());
	}

	@Override
	public void importTile(Tile tile, TileTransform tileTransform) {
		LongPoint tilePosition = tileTransform.transform(tile.getPosition());
		// Edges
		for (Orientation orientation : Orientation.values()) {
			// Get edge type
			EdgeType edgeType = tile.getEdgeAt(orientation).getType();
			// Ignore unknown edges
			if (edgeType == EdgeType.UNKNOWN)
				continue;
			// Place edge
			setEdge(tilePosition, tileTransform.transform(orientation), edgeType);
		}
		// Barcode
		if (tile.hasBarcode()) {
			setBarcode(tilePosition, tile.getBarcode());
		}
		// Explored
		if (tile.isExplored()) {
			setExplored(tilePosition);
		}
		// Seesaw
		if (tile.isSeesaw()) {
			setSeesaw(tilePosition, tile.getSeesawBarcode());
		}
	}

	@Override
	public void importTiles(Iterable<Tile> tiles) {
		importTiles(tiles, TileTransform.getIdentity());
	}

	@Override
	public void importTiles(Iterable<Tile> tiles, TileTransform tileTransform) {
		for (Tile tile : tiles) {
			importTile(tile, tileTransform);
		}
	}

	@Override
	public void setEdge(LongPoint tilePosition, Orientation orientation, Edge.EdgeType type) {
		Tile tile = getTileAt(tilePosition);
		Edge edge = tile.getEdgeAt(orientation);

		// Set edge
		if (edge.getType() == type)
			return;
		edge.setType(type);

		// Fire edge changed event
		fireEdgeChanged(edge);

		// Fire tile changed events
		for (LongPoint touchingPosition : edge.getTouching()) {
			fireTileChanged(getTileAt(touchingPosition));
		}
	}

	@Override
	public void setTileShape(LongPoint tilePosition, TileShape shape) {
		for (Orientation orientation : shape.getType().getWalls(shape.getOrientation())) {
			setEdge(tilePosition, orientation, EdgeType.WALL);
		}
		for (Orientation orientation : shape.getType().getOpenings(shape.getOrientation())) {
			setEdge(tilePosition, orientation, EdgeType.OPEN);
		}
	}

	@Override
	public void setBarcode(LongPoint position, Barcode barcode) throws IllegalStateException {
		Tile tile = getTileAt(position);

		// Set barcode
		if (tile.hasBarcode() && tile.getBarcode().equals(barcode))
			return;
		tile.setBarcode(barcode);

		// Fire tile changed event
		fireTileChanged(tile);
	}

	@Override
	public void setBarcode(LongPoint position, byte barcode) throws IllegalStateException {
		setBarcode(position, new Barcode(barcode));
	}

	@Override
	public Tile getBarcodeTile(Barcode barcode) {
		checkNotNull(barcode);

		for (Tile tile : tiles.values()) {
			if (tile.hasBarcode() && tile.getBarcode().equals(barcode)) {
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
	public Seesaw getSeesaw(Barcode barcode) {
		return seesaws.get(barcode);
	}

	@Override
	public Seesaw getSeesaw(byte barcode) {
		return getSeesaw(new Barcode(barcode));
	}

	@Override
	public Seesaw getOrCreateSeesaw(Barcode barcode) {
		checkNotNull(barcode);

		Seesaw seesaw = getSeesaw(barcode);
		if (seesaw == null) {
			seesaw = new Seesaw(barcode);
			registerSeesaw(seesaw);
		}
		return seesaw;
	}

	@Override
	public Seesaw getOrCreateSeesaw(byte barcode) {
		return getOrCreateSeesaw(new Barcode(barcode));
	}

	@Override
	public void setSeesaw(LongPoint tilePosition, Barcode seesawBarcode) {
		Tile tile = getTileAt(tilePosition);

		// Set seesaw
		if (tile.isSeesaw() && tile.getSeesawBarcode().equals(seesawBarcode))
			return;
		tile.setSeesaw(getOrCreateSeesaw(seesawBarcode), seesawBarcode);

		// Fire tile changed event
		fireTileChanged(tile);
	}

	private void registerSeesaw(Seesaw seesaw) {
		seesaws.put(seesaw.getLowestBarcode(), seesaw);
		seesaws.put(seesaw.getHighestBarcode(), seesaw);
	}

	@Override
	public Tile getSeesawTile(Barcode barcode) {
		checkNotNull(barcode);

		for (Tile tile : tiles.values()) {
			if (tile.isSeesaw() && tile.getSeesawBarcode().equals(barcode)) {
				return tile;
			}
		}

		return null;
	}

	@Override
	public void setExplored(LongPoint position) {
		Tile tile = getTileAt(position);

		// Set explored
		if (tile.isExplored())
			return;
		tile.setExplored();

		// Fire tile explored event
		fireTileExplored(tile);
	}

	@Override
	public void clear() {
		tiles.clear();
		targets.clear();
		fireMazeCleared();
	}

	@Override
	public void addListener(MazeListener listener) {
		checkNotNull(listener);
		listeners.add(listener);
	}

	@Override
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

	@Override
	public Point toAbsolute(Point relativePosition) {
		checkNotNull(relativePosition);
		return originTransform.transform(relativePosition);
	}

	@Override
	public float toAbsolute(float relativeHeading) {
		return originTransform.transform(relativeHeading);
	}

	@Override
	public Pose toAbsolute(Pose relativePose) {
		checkNotNull(relativePose);
		return originTransform.transform(relativePose);
	}

	@Override
	public Point toRelative(Point absolutePosition) {
		checkNotNull(absolutePosition);
		return originTransform.inverseTransform(absolutePosition);
	}

	@Override
	public float toRelative(float absoluteHeading) {
		return originTransform.inverseTransform(absoluteHeading);
	}

	@Override
	public Pose toRelative(Pose absolutePose) {
		checkNotNull(absolutePose);
		return originTransform.inverseTransform(absolutePose);
	}

	@Override
	public Point toTile(Point relativePosition) {
		double x = relativePosition.getX() / getTileSize();
		double y = relativePosition.getY() / getTileSize();
		return new Point((float) x, (float) y);
	}

	@Override
	public Point fromTile(Point tilePosition) {
		return tilePosition.multiply(getTileSize());
	}

	@Override
	public Point getTileCenter(LongPoint tilePosition) {
		return fromTile(tilePosition.toPoint().add(new Point(0.5f, 0.5f)));
	}

	@Override
	public Geometry getEdgeGeometry() {
		return edgeGeometry.getGeometry();
	}

	@Override
	public Line getEdgeLine(Edge edge) {
		return createEdgeLine(edge);
	}

	@Override
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

	@Override
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

	@Override
	public Tile getTarget(Target target) {
		return getTileAt(targets.get(target));
	}

	@Override
	public void setTarget(Target target, Tile tile) {
		checkNotNull(target);
		checkNotNull(tile);
		targets.put(target, tile.getPosition());
	}

	@Override
	public Pose getStartPose(int playerNumber) {
		return startPoses.get(playerNumber);
	}

	@Override
	public void setStartPose(int playerNumber, Pose pose) {
		startPoses.put(playerNumber, checkNotNull(pose));
	}

	@Override
	public void setStartPose(int playerNumber, LongPoint tilePosition, Orientation orientation) {
		// Center on tile
		Point relativePosition = getTileCenter(tilePosition);
		float relativeAngle = orientation.getAngle();
		// Create and set pose
		Pose pose = new Pose();
		pose.setLocation(relativePosition);
		pose.setHeading(relativeAngle);
		// Transform to absolute coordinates
		pose = toAbsolute(pose);
		setStartPose(playerNumber, pose);
	}

}
