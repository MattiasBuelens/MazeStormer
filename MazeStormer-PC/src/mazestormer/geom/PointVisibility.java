package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
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
import com.vividsolutions.jts.geom.Polygon;

public abstract class PointVisibility {

	protected final Coordinate viewCoord;
	protected final GeometryFactory factory;

	public static final double TOLERANCE = 1e-5d;

	protected PointVisibility(GeometryFactory factory, Coordinate viewCoord)
			throws IllegalArgumentException {
		this.factory = checkNotNull(factory);
		this.viewCoord = checkNotNull(viewCoord);
	}

	/**
	 * Collect line segments from the given polygon.
	 * 
	 * @param polygon
	 *            The polygon.
	 * @return All line segments of the polygon.
	 */
	protected List<LineSegment> collect(Polygon polygon) {
		List<LineSegment> segments = new ArrayList<LineSegment>(
				polygon.getNumPoints());
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
	protected List<LineSegment> collect(LinearRing ring) {
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
	protected List<LineSegment> collect(LineString ring) {
		return collect(toLinearRing(ring));
	}

	protected LinearRing toLinearRing(LineString string) {
		if (string instanceof LinearRing) {
			return (LinearRing) string;
		} else {
			return factory.createLinearRing(string.getCoordinateSequence());
		}
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
	protected MultiLineString getProjections(Geometry polygons,
			LineSegment screen) {
		int numGeometries = polygons.getNumGeometries();
		MultiFractionSegment projectedSegments = new MultiFractionSegment();
		// Iterate over polygons
		for (int i = 0; i < numGeometries; ++i) {
			Geometry geometry = polygons.getGeometryN(i);
			// Dirty fix
			if (!(geometry instanceof Polygon))
				continue;
			Polygon polygon = (Polygon) geometry;
			// Ignore empty polygons
			if (polygon.isEmpty())
				continue;
			// Project polygon on edge
			List<FractionSegment> projected = project(polygon, screen);
			// Store projection
			projectedSegments.addSegments(projected);
		}
		// Create line segments along screen
		List<LineSegment> projections = projectedSegments.combine()
				.segmentsAlong(screen);
		// Make line strings
		LineString[] projectedLines = new LineString[projections.size()];
		for (int i = 0; i < projectedLines.length; ++i) {
			projectedLines[i] = projections.get(i).toGeometry(factory);
		}
		// Combine line strings
		return factory.createMultiLineString(projectedLines);
	}

	/**
	 * Project a polygon onto a screen.
	 * 
	 * @param polygon
	 *            The polygon to project.
	 * @param screen
	 *            The screen on which to project.
	 * @return The segments of the screen containing the projection.
	 */
	protected List<FractionSegment> project(Polygon polygon, LineSegment screen) {
		// Only check exterior shell, interior holes don't matter for projection
		List<LineSegment> segments = collect(toLinearRing(polygon
				.getExteriorRing()));
		List<FractionSegment> projectedSegments = new ArrayList<FractionSegment>(
				segments.size());
		for (LineSegment segment : segments) {
			// Project vertex onto screen
			double leftFraction = project(segment.getCoordinate(0), screen);
			double rightFraction = project(segment.getCoordinate(1), screen);
			FractionSegment projectedSegment = new FractionSegment(
					leftFraction, rightFraction);
			projectedSegments.add(projectedSegment);
		}
		return projectedSegments;
	}

	protected double project(Coordinate point, LineSegment screen) {
		// Project vertex onto screen
		LineSegment ray = new LineSegment(viewCoord, point);
		Coordinate projection = screen.lineIntersection(ray);
		// If lines overlap
		if (projection == null) {
			return 1d;
		}
		return screen.projectionFactor(projection);
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
	protected Polygon getViewingTriangle(Coordinate leftVertex,
			Coordinate rightVertex) {
		return factory.createPolygon(new Coordinate[] { viewCoord, leftVertex,
				rightVertex, viewCoord });
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
	 * Reduce the amount of segments of a {@link MultiLineString}.
	 * 
	 * @param multiLineString
	 *            The {@link MultiLineString} to reduce.
	 */
	protected MultiLineString reduceMultiLineString(
			MultiLineString multiLineString) {
		// Union
		Geometry reduced = multiLineString.union();
		// Make appropriate result
		if (reduced instanceof MultiLineString) {
			return (MultiLineString) reduced;
		} else if (reduced instanceof LineString) {
			return factory
					.createMultiLineString(new LineString[] { (LineString) reduced });
		} else {
			throw new RuntimeException(
					"Unexpected segments combination result type: "
							+ reduced.getClass());
		}
	}

}
