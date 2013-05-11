package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Tile {

	private final LongPoint position;
	private final Map<Orientation, Edge> edges = new EnumMap<Orientation, Edge>(Orientation.class);

	/*
	 * Exploration
	 */
	private boolean isExplored = false;
	private boolean ignoreFlag = false;

	/*
	 * Barcode
	 */
	private Barcode barcode;

	/*
	 * Seesaw
	 */
	private Seesaw seesaw;
	private Barcode seesawBarcode;
	private Orientation seesawOrientation;

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
		setExplored(true);
	}

	private void setExplored(boolean isExplored) {
		this.isExplored = isExplored;
	}

	public boolean hasBarcode() {
		return getBarcode() != null;
	}

	public Barcode getBarcode() {
		return barcode;
	}

	public void setBarcode(Barcode barcode) throws IllegalStateException {
		if (!getShape().getType().supportsBarcode())
			throw new IllegalStateException("Tile type does not support barcodes.");
		this.barcode = barcode;
	}

	public Orientation orientationTo(Tile otherTile) {
		for (Orientation orientation : Orientation.values()) {
			LongPoint neighborPosition = orientation.shift(this.getPosition());
			if (otherTile.getPosition().equals(neighborPosition))
				return orientation;
		}
		return null;
	}

	public boolean isSeesaw() {
		return getSeesaw() != null;
	}

	public Seesaw getSeesaw() {
		return seesaw;
	}

	public Barcode getSeesawBarcode() {
		return seesawBarcode;
	}

	public Orientation getSeesawOrientation() {
		return seesawOrientation;
	}

	public void setSeesawOrientation(Orientation seesawOrientation) {
		this.seesawOrientation = seesawOrientation;
	}

	public void setSeesaw(Seesaw seesaw, Barcode seesawBarcode) {
		this.seesaw = seesaw;
		this.seesawBarcode = seesawBarcode;
	}

	/**
	 * Deze methode alleen opvragen in de wereldsimulator, dus in de sourceMaze,
	 * fysiek moet gebruik gemaakt worden van de ir-sensor en sophie's bal.
	 * Virtueel moet dit onrechtstreeks wel naar hier komen via een
	 * VirtualIRSensor.
	 */
	public boolean isSeesawOpen() {
		return seesaw.isOpen(seesawBarcode);
	}

	public boolean getIgnoreFlag() {
		return this.ignoreFlag;
	}

	public void setIgnoreFlag(boolean flag) {
		this.ignoreFlag = flag;
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

	public boolean isNeighbourTo(Tile otherTile) {
		Orientation orientation = orientationTo(otherTile);
		return orientation != null && getEdgeAt(orientation).getType() == EdgeType.OPEN;
	}

}
