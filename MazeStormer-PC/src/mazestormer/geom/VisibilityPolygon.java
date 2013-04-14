package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.math.DoubleMath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Computes the visibility polygon from a view point inside a {@link Polygon}.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Visibility_polygon">Wikipedia</a>
 */
public class VisibilityPolygon {

	private final Polygon polygon;
	private final Coordinate viewCoord;
	private final GeometryFactory factory;

	private static final double TOLERANCE = 1e-5d;

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
		checkNotNull(polygon);
		checkNotNull(viewCoord);

		this.polygon = polygon;
		this.factory = polygon.getFactory();
		this.viewCoord = viewCoord;
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

	/**
	 * Compute the visibility region.
	 */
	public Geometry build() {
		// Exterior shell
		Geometry result = build(polygon.getExteriorRing());
		// Interior holes
		for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
			result = result.union(build(polygon.getInteriorRingN(i)));
		}
		return result;
	}

	/**
	 * Compute the visible region from the view point on the given ring.
	 * 
	 * @param ring
	 *            The ring.
	 */
	private Geometry build(LinearRing ring) {
		Geometry result = GeometryUtils.emptyPolygon(factory);
		Coordinate[] coords = ring.getCoordinates();
		// The first and last vertices are equal in a closed ring
		// so make sure to not treat those as an edge
		for (int i = 0; i < coords.length - 1; ++i) {
			// i = vertex index
			// j = next vertex index
			final int j = (i + 1) % coords.length;
			LineSegment edge = new LineSegment(coords[i], coords[j]);
			result = result.union(getVisibleRegion(edge));
		}
		return result;
	}

	/**
	 * Compute the visible region from the view point on the given ring.
	 * 
	 * @param ring
	 *            The ring.
	 * @see #build(LinearRing)
	 */
	private Geometry build(LineString ring) {
		if (!ring.isClosed())
			throw new IllegalArgumentException("Ring must be closed.");
		if (!ring.isSimple())
			throw new IllegalArgumentException("Ring must be simple.");
		return build(factory.createLinearRing(ring.getCoordinateSequence()));
	}

	/**
	 * Get the visible region between the view point and the given edge.
	 * 
	 * @param screen
	 *            The line segment on which to project.
	 * @return The region containing all points visible between the view point
	 *         and the edge.
	 */
	private Geometry getVisibleRegion(LineSegment screen) {
		// Start with all points between view point and screen
		Geometry result = getViewingTriangle(screen);
		// Find collisions with polygon
		Geometry collisions = result.difference(polygon);
		// Iterate over colliding polygons
		for (int i = 0; i < collisions.getNumGeometries(); ++i) {
			Polygon collision = (Polygon) collisions.getGeometryN(i);
			// Ignore empty collisions
			if (collision.isEmpty())
				continue;
			// Project polygon on edge
			LineSegment projectedLine = project(collision, screen);
			// Subtract projection from result
			Geometry projection = getViewingTriangle(projectedLine);
			result = result.difference(projection);
			// Exit if area too small
			if (DoubleMath.fuzzyEquals(result.getArea(), 0d, TOLERANCE)) {
				return GeometryUtils.emptyPolygon(factory);
			}
		}
		return result;
	}

	/**
	 * Project a polygon onto a screen.
	 * 
	 * @param polygon
	 *            The polygon to project.
	 * @param screen
	 *            The screen on which to project.
	 * @return The section of the screen containing the projection.
	 */
	private LineSegment project(Polygon polygon, LineSegment screen) {
		double minFraction = 1.0, maxFraction = 0.0;
		// Only check exterior shell, interior holes don't matter for projection
		Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
		for (Coordinate coord : coords) {
			// Project vertex onto screen
			LineSegment ray = new LineSegment(viewCoord, coord);
			Coordinate projection = screen.lineIntersection(ray);
			double fraction = screen.projectionFactor(projection);
			// Update minimum and maximum
			if (fraction < minFraction) {
				minFraction = fraction;
			}
			if (fraction > maxFraction) {
				maxFraction = fraction;
			}
		}
		// Produce line segment as a section of the screen
		Coordinate minCoord = screen.pointAlong(minFraction);
		Coordinate maxCoord = screen.pointAlong(maxFraction);
		return new LineSegment(minCoord, maxCoord);
	}

	/**
	 * Get the triangle containing all points between the view point and the
	 * given edge.
	 * 
	 * @param leftVertex
	 *            The left vertex of the edge.
	 * @param rightVertex
	 *            The right vertex of the edge.
	 * @return A triangle connecting the view point to the two given vertices.
	 */
	private Polygon getViewingTriangle(Coordinate leftVertex, Coordinate rightVertex) {
		return factory.createPolygon(new Coordinate[] { viewCoord, leftVertex, rightVertex, viewCoord });
	}

	/**
	 * Get the triangle containing all points between the view point and the
	 * given edge.
	 * 
	 * @param edge
	 *            The edge.
	 * @return A triangle connecting the view point to the given edge.
	 * @see #getViewingTriangle(Coordinate, Coordinate)
	 */
	private Polygon getViewingTriangle(LineSegment edge) {
		return getViewingTriangle(edge.getCoordinate(0), edge.getCoordinate(1));
	}

}
