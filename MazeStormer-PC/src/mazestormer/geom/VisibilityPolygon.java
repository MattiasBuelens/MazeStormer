package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

/**
 * Computes the visibility polygon from a view point inside a {@link Polygon}.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Visibility_polygon">Wikipedia</a>
 */
public class VisibilityPolygon extends PointVisibility {

	protected final Polygon polygon;

	/**
	 * Create a new visibility polygon construction.
	 * 
	 * @param polygon
	 *            The polygon.
	 * @param viewCoord
	 *            The coordinate of the view point.
	 * @throws NullPointerException
	 *             If the polygon and/or view point are not effective.
	 * @throws IllegalArgumentException
	 *             If the polygon is {@link Polygon#isEmpty() empty}.
	 * @throws IllegalArgumentException
	 *             If the polygon is not {@link Polygon#isSimple() simple}.
	 * @throws IllegalArgumentException
	 *             If the polygon does not {@link Polygon#contains() contain}
	 *             the view point.
	 */
	public VisibilityPolygon(Polygon polygon, Coordinate viewCoord) throws IllegalArgumentException {
		super(checkNotNull(polygon).getFactory(), checkNotNull(viewCoord));

		this.polygon = polygon;
		Point viewPoint = factory.createPoint(viewCoord);

		if (polygon.isEmpty()) {
			throw new IllegalArgumentException("Polygon cannot be empty.");
		}
		if (!polygon.isSimple()) {
			throw new IllegalArgumentException("Polygon must be simple.");
		}
		if (!polygon.contains(viewPoint)) {
			throw new IllegalArgumentException("Polygon must contain base point.");
		}
	}

	public static Geometry build(Polygon polygon, Coordinate viewCoord) {
		return new VisibilityPolygon(polygon, viewCoord).build();
	}

	/**
	 * Compute the visibility region.
	 */
	public Geometry build() {
		// Collect line segments
		List<LineSegment> segments = collect(polygon);

		// Execute projections
		Collection<Geometry> regions = getVisibleRegions(segments);

		// Combine and simplyify
		return combine(regions);
	}

	/**
	 * Get the visible region between the view point and the given edges.
	 * 
	 * @param edges
	 *            The edges on which to project.
	 */
	protected Collection<Geometry> getVisibleRegions(List<LineSegment> edges) {
		List<Geometry> regions = new LinkedList<Geometry>();
		getVisibleRegions(edges, regions);
		return regions;
	}

	/**
	 * Get the visible region between the view point and the given edges.
	 * 
	 * @param edges
	 *            The edges on which to project.
	 * @param regions
	 *            The regions collection receiving the output.
	 */
	protected final void getVisibleRegions(List<LineSegment> edges, Collection<Geometry> regions) {
		for (LineSegment segment : edges) {
			regions.addAll(getVisibleRegions(segment));
		}
	}

	/**
	 * Get the visible region between the view point and the given edge.
	 * 
	 * @param screen
	 *            The line segment on which to project.
	 * @return The regions containing all points visible between the view point
	 *         and the edge.
	 */
	protected Collection<Geometry> getVisibleRegions(LineSegment screen) {
		// Find visible segment parts
		Geometry visible = getVisibleSegments(screen);
		// Exit if empty
		if (visible.isEmpty()) {
			return Collections.emptySet();
		}
		// Build triangles
		return buildViewingTriangles(visible);
	}

	/**
	 * Get the visible segments on the screen.
	 * 
	 * @param obstacles
	 *            The obstacles.
	 * @param screen
	 *            The line segment on which to project.
	 * @return The segments visible from the view point and the edge.
	 */
	protected Geometry getVisibleSegments(LineSegment screen) {
		Geometry screenGeom = screen.toGeometry(factory);
		// Start with all points between view point and screen
		Geometry view = getViewingTriangle(screen);
		// Find collisions within view
		Geometry collisions = view.difference(polygon);
		// Exit if no collisions
		if (collisions.isEmpty()) {
			return screenGeom;
		}
		// Get and process projections
		Geometry blocked = getProjections(collisions, screen);
		blocked = GeometryPrecisionReducer.reduce(blocked, new PrecisionModel(1e3));
		blocked = blocked.union();
		// Return blocked segments
		return screenGeom.difference(blocked);
	}

	/**
	 * Combine the given visible regions into one geometry.
	 * 
	 * @param regions
	 *            The visible regions.
	 */
	protected Geometry combine(Collection<Geometry> regions) {
		Geometry result = factory.buildGeometry(regions);
		result = GeometryUtils.removeCollinear(result);
		return result;
	}

}
