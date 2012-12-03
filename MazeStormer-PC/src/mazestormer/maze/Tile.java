package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import lejos.geom.Line;
import lejos.geom.Point;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Tile {

	private final LongPoint position;
	private final EnumMap<Orientation, Edge> edges = new EnumMap<Orientation, Edge>(
			Orientation.class);
	private boolean isExplored = false;
	private Barcode barcode;

	public Tile(LongPoint position) {
		this.position = new LongPoint(position);
		// Fill edges with unlinked unknown edges
		for (Orientation orientation : Orientation.values()) {
			setEdge(new Edge(getPosition(), orientation));
		}
	}

	public long getX() {
		return (long) position.getX();
	}

	public long getY() {
		return (long) position.getY();
	}

	public LongPoint getPosition() {
		return new LongPoint(position);
	}

	public Collection<Edge> getEdges() {
		return Collections.unmodifiableCollection(edges.values());
	}

	public boolean hasEdge(Edge edge) {
		checkNotNull(edge);
		return edges.containsValue(edge);
	}

	public Edge getEdgeAt(Orientation side) {
		checkNotNull(side);
		return edges.get(side);
	}

	public void setEdge(Edge edge) {
		edges.put(edge.getOrientationFrom(getPosition()), edge);
	}

	public void setEdge(Orientation direction, Edge.EdgeType type) {
		getEdgeAt(direction).setType(type);
	}

	public boolean isExplored() {
		return isExplored;
	}

	public void setExplored() {
		isExplored = true;
	}

	public TileShape getShape() {
		return TileShape.get(getClosedSides());
	}

	private EnumSet<Orientation> getSidesByType(EdgeType type) {
		EnumSet<Orientation> result = EnumSet.noneOf(Orientation.class);
		for (Map.Entry<Orientation, Edge> entry : edges.entrySet()) {
			Orientation orientation = entry.getKey();
			Edge edge = entry.getValue();
			if (edge.getType() == type) {
				result.add(orientation);
			}
		}
		return result;
	}

	public EnumSet<Orientation> getClosedSides() {
		return getSidesByType(EdgeType.WALL);
	}

	public EnumSet<Orientation> getOpenSides() {
		return getSidesByType(EdgeType.OPEN);
	}

	public EnumSet<Orientation> getUnknownSides() {
		return getSidesByType(EdgeType.UNKNOWN);
	}

	/**
	 * Returns the rectangle in relative coordinates to the maze.
	 */
	public Rectangle2D getSide(Orientation orientation, Maze maze) {
		// Get edge points in tile coordinates
		Line line = orientation.getLine();
		Point tilePosition = getPosition().toPoint();
		Point p1 = line.getP1().add(tilePosition);
		Point p2 = line.getP2().add(tilePosition);

		// Convert to relative coordinates
		p1 = maze.fromTile(p1);
		p2 = maze.fromTile(p2);

		// Shift points to account for edge size
		float halfLineThickness = maze.getEdgeSize() / 2f;
		Point shift = new Point(halfLineThickness, halfLineThickness);
		p1 = p1.subtract(shift);
		p2 = p2.add(shift);
		// Return bounding box
		return new Rectangle2D.Double(p1.getX(), p1.getY(), p2.getX()
				- p1.getX(), p2.getY() - p1.getY());
	}
	
	public void setBarcode(byte value) throws IllegalStateException {
		if(getTileShape().getType() != TileType.STRAIGHT)
			throw new IllegalStateException("Tile doesn't have the right type.");
		barcode = new Barcode(value);
	}
	
	public byte getBarcodeValue() {
		return barcode.getValue();
	}
	
	private TileShape getTileShape() {
		return TileShape.get(getClosedSides());
	}
}
