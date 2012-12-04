package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Tile {

	private final LongPoint position;
	private final Map<Orientation, Edge> edges = new EnumMap<Orientation, Edge>(
			Orientation.class);
	private Barcode barcode;
	private boolean isExplored = false;

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

	public boolean hasBarcode() {
		return getBarcode() != null;
	}

	public Barcode getBarcode() {
		return barcode;
	}

	public void setBarcode(Barcode barcode) throws IllegalStateException {
		if (!getShape().getType().supportsBarcode())
			throw new IllegalStateException(
					"Tile type does not support barcodes.");
		this.barcode = barcode;
	}

	public TileShape getShape() {
		return TileShape.get(getClosedSides());
	}

	public boolean isExplored() {
		return isExplored;
	}

	public void setExplored() {
		isExplored = true;
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

}
