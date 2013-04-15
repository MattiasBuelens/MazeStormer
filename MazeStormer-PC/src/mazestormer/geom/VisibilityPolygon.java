package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.math.DoubleMath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Computes the visibility polygon from a view point inside a {@link Polygon}.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Visibility_polygon">Wikipedia</a>
 */
public class VisibilityPolygon {

	protected final Polygon polygon;
	protected final Coordinate viewCoord;
	protected final GeometryFactory factory;

	protected static final double TOLERANCE = 1e-5d;

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
		// Collect line segments
		List<LineSegment> segments = collect(polygon);

		// Execute projections
		Collection<Geometry> regions = getVisibleRegions(segments);

		// Combine and simplyify
		return combine(regions);
	}

	/**
	 * Collect line segments from the given polygon.
	 * 
	 * @param polygon
	 *            The polygon.
	 * @return All line segments of the polygon.
	 */
	public static List<LineSegment> collect(Polygon polygon) {
		List<LineSegment> segments = new ArrayList<LineSegment>(polygon.getNumPoints());
		// Exterior shell
		segments.addAll(collect(polygon.getExteriorRing()));
		// Interior holes
		for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
			segments.addAll(collect(polygon.getInteriorRingN(i)));
		}
		return segments;
	}

	/**
	 * Collect line segments from the given ring.
	 * 
	 * @param ring
	 *            The ring.
	 * @return All line segments of the ring.
	 */
	public static List<LineSegment> collect(LinearRing ring) {
		Coordinate[] coords = ring.getCoordinates();
		// The first and last vertices are equal in a closed ring
		// so make sure to not treat those as an edge
		int numPoints = coords.length - 1;
		List<LineSegment> segments = new ArrayList<LineSegment>(numPoints);
		for (int i = 0; i < numPoints; ++i) {
			// i = vertex index
			// j = next vertex index
			final int j = (i + 1) % coords.length;
			LineSegment edge = new LineSegment(coords[i], coords[j]);
			segments.add(edge);
		}
		return segments;
	}

	/**
	 * Collect line segments from the given ring.
	 * 
	 * @param ring
	 *            The ring.
	 * @return All line segments of the ring.
	 */
	public static List<LineSegment> collect(LineString ring) {
		return collect(toLinearRing(ring));
	}

	public static LinearRing toLinearRing(LineString string) {
		if (string instanceof LinearRing) {
			return (LinearRing) string;
		} else {
			return string.getFactory().createLinearRing(string.getCoordinateSequence());
		}
	}

	/**
	 * Get the visible region between the view point and the given edges.
	 * 
	 * @param edges
	 *            The edges on which to project.
	 */
	protected Collection<Geometry> getVisibleRegions(List<LineSegment> edges) {
		List<Geometry> regions = new LinkedList<Geometry>();
		for (LineSegment segment : edges) {
			regions.addAll(getVisibleRegions(segment));
		}
		return regions;
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
		// Start with all points between view point and screen
		Geometry view = getViewingTriangle(screen);
		// Find collisions with polygon
		Geometry collisions = view.difference(polygon);
		// Exit if no collisions
		if (collisions.isEmpty()) {
			return Collections.singleton(view);
		}
		// Remove blocked segments
		Geometry blocked = getProjections(collisions, screen);
		Geometry visible = screen.toGeometry(factory);
		visible = visible.difference(blocked);
		// Exit if empty
		if (visible.isEmpty()) {
			return Collections.emptySet();
		}
		// Build triangles
		return buildViewingTriangles(visible);
	}

	/**
	 * Get the projections of all polygons in the given {@link Polygon} or
	 * {@link MultiPolygon} onto the given screen.
	 * 
	 * @param polygons
	 *            The polygons to project.
	 * @param screen
	 *            The line segment on which to project.
	 * @return The projected line strings.
	 * @see #project(Polygon, LineSegment)
	 */
	protected Geometry getProjections(Geometry polygons, LineSegment screen) {
		int numGeometries = polygons.getNumGeometries();
		List<LineString> projections = new ArrayList<LineString>(numGeometries);
		// Iterate over polygons
		for (int i = 0; i < numGeometries; ++i) {
			Polygon polygon = (Polygon) polygons.getGeometryN(i);
			// Ignore empty polygons
			if (polygon.isEmpty())
				continue;
			// Project polygon on edge
			LineString projectedLine = project(polygon, screen).toGeometry(factory);
			// Store projection
			projections.add(projectedLine);
		}
		return factory.createMultiLineString(GeometryFactory.toLineStringArray(projections));
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
	protected LineSegment project(Polygon polygon, LineSegment screen) {
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
	 * Build the viewing triangles from the view point to all segments of the
	 * given {@link LineString} or {@link MultiLineString}.
	 * 
	 * @param lineStrings
	 *            The (visible) line strings.
	 * @return A collection of viewing triangles.
	 * @see #getViewingTriangle(LineSegment)
	 */
	protected Collection<Geometry> buildViewingTriangles(Geometry lineStrings) {
		int numGeometries = lineStrings.getNumGeometries();
		List<Geometry> triangles = new ArrayList<Geometry>(numGeometries);
		for (int i = 0; i < numGeometries; ++i) {
			Geometry line = lineStrings.getGeometryN(i);
			// Add viewing triangle
			Coordinate[] coords = line.getCoordinates();
			assert (coords.length == 2);
			Polygon triangle = getViewingTriangle(coords[0], coords[1]);
			// Ignore too small triangles
			if (!DoubleMath.fuzzyEquals(triangle.getArea(), 0d, TOLERANCE)) {
				triangles.add(triangle);
			}
		}
		return triangles;
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
	protected Polygon getViewingTriangle(Coordinate leftVertex, Coordinate rightVertex) {
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
	protected Polygon getViewingTriangle(LineSegment edge) {
		return getViewingTriangle(edge.getCoordinate(0), edge.getCoordinate(1));
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
