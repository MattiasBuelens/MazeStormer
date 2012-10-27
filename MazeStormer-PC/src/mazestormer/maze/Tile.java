package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.EnumMap;

import lejos.geom.Point;

public class Tile {

	private final Point position;
	private final EnumMap<Orientation, Edge> edges = new EnumMap<Orientation, Edge>(
			Orientation.class);

	public Tile(Point position) {
		this.position = position;
	}

	public Point getPosition() {
		return position;
	}

	public Iterable<Edge> getEdges() {
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
}
