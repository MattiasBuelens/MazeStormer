package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;

import lejos.geom.Line;
import lejos.geom.Point;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.LongPoint;

public class Tile {

	private final LongPoint position;
	private final EnumMap<Orientation, Edge> edges = new EnumMap<Orientation, Edge>(
			Orientation.class);
	private boolean isExplored = false;

	public Tile(LongPoint position) {
		this.position = new LongPoint(position);
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

	public boolean hasEdgeAt(Orientation side) {
		checkNotNull(side);
		return edges.containsKey(side);
	}

	public Edge getEdgeAt(Orientation side) {
		checkNotNull(side);
		return edges.get(side);
	}

	public void addEdge(Edge edge) {
		checkNotNull(edge);
		edges.put(edge.getOrientationFrom(getPosition()), edge);
	}

	public void addEdges(Iterable<Edge> edges) {
		for (Edge edge : edges) {
			addEdge(edge);
		}
	}

	//TODO: nodig?
	public boolean hasUnknownEdges(){
		for(Edge currentEdge : getEdges()){
			if(currentEdge.getType() == EdgeType.UNKNOWN){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isExplored(){
		return isExplored;
	}
	
	public void setExplored(){
		isExplored = true;
	}
	
	public EnumSet<Orientation> getClosedSides() {
		EnumSet<Orientation> result = EnumSet.noneOf(Orientation.class);
		for (Orientation orientation : Orientation.values()) {
			if (hasEdgeAt(orientation)) {
				result.add(orientation);
			}
		}
		return result;
	}

	public EnumSet<Orientation> getOpenSides() {
		return EnumSet.complementOf(getClosedSides());
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
}
