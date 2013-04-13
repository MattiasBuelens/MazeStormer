package mazestormer.maze;

import java.awt.geom.Rectangle2D;

import mazestormer.maze.Edge.EdgeType;
import mazestormer.util.GeometryUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class EdgeGeometry extends DefaultMazeListener {

	private final IMaze maze;

	private final GeometryFactory geomFact;
	private Geometry geom;

	public EdgeGeometry(IMaze maze) {
		this.maze = maze;
		this.geomFact = new GeometryFactory();
		reset();
	}

	public final IMaze getMaze() {
		return maze;
	}

	public final Geometry getGeometry() {
		return geom;
	}

	/**
	 * Add an edge to the geometry.
	 * 
	 * @param edge
	 *            The edge to add.
	 */
	private void addEdge(Edge edge) {
		if (edge.getType() == EdgeType.WALL) {
			// Get the edge boundaries
			Rectangle2D rect = getMaze().getEdgeBounds(edge);
			// Add to geometry
			Polygon poly = GeometryUtils.toGeometry(rect, geomFact);
			geom = geom.union(poly);
		}
	}

	/**
	 * Reset the geometry.
	 */
	private final void reset() {
		geom = geomFact.createPolygon(null, null);
	}

	@Override
	public void edgeChanged(Edge edge) {
		addEdge(edge);
	}

	@Override
	public void mazeCleared() {
		reset();
	}

}
