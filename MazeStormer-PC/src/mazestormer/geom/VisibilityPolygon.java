package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.math.DoubleMath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class VisibilityPolygon {

	private final Polygon polygon;
	private final Coordinate viewCoord;
	private final GeometryFactory factory;

	private static final double TOLERANCE = 1e-5d;

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

	public Geometry build() {
		Geometry result = build(polygon.getExteriorRing().getCoordinates());
		for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
			result = result.union(build(polygon.getInteriorRingN(i).getCoordinates()));
		}
		return result;
	}

	private Geometry build(Coordinate[] coords) {
		Geometry result = factory.createPolygon(null, null);
		for (int i = 0; i < coords.length - 1; ++i) {
			final int j = (i + 1) % coords.length;
			result = result.union(projectOn(coords[i], coords[j]));
		}
		return result;
	}

	private Geometry projectOn(Coordinate p1, Coordinate p2) {
		Geometry result = getProjectingTriangle(p1, p2);
		// Line segment on which to project
		LineSegment screen = new LineSegment(p1, p2);
		// Find collisions with polygon
		Geometry diff = result.difference(polygon);
		for (int i = 0; i < diff.getNumGeometries(); ++i) {
			Polygon diffPoly = (Polygon) diff.getGeometryN(i);
			// Ignore empty polygons
			if (diffPoly.isEmpty())
				continue;
			// Project polygon on edge
			Coordinate[] projCoords = project(diffPoly, screen);
			// Subtract projection from edge string
			Geometry diffTriangle = getProjectingTriangle(projCoords);
			result = result.difference(diffTriangle);
			// Stop if area too small
			if (DoubleMath.fuzzyEquals(result.getArea(), 0d, TOLERANCE)) {
				return factory.createPolygon(null, null);
			}
		}
		return result;
	}

	private Coordinate[] project(Polygon polygon, LineSegment screen) {
		double minFraction = 1.0, maxFraction = 0.0;
		Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
		for (Coordinate coord : coords) {
			LineSegment ray = new LineSegment(viewCoord, coord);
			Coordinate projection = screen.lineIntersection(ray);
			double fraction = screen.projectionFactor(projection);
			if (fraction < minFraction)
				minFraction = fraction;
			if (fraction > maxFraction)
				maxFraction = fraction;
		}
		Coordinate minCoord = screen.pointAlong(minFraction);
		Coordinate maxCoord = screen.pointAlong(maxFraction);
		return new Coordinate[] { minCoord, maxCoord };
	}

	private Polygon getProjectingTriangle(Coordinate p1, Coordinate p2) {
		return factory.createPolygon(new Coordinate[] { viewCoord, p1, p2, viewCoord });
	}

	private Polygon getProjectingTriangle(Coordinate... coords) {
		return getProjectingTriangle(coords[0], coords[1]);
	}

	public boolean isVisibleFrom(Coordinate polygonVertex, Coordinate basePoint) {
		LineString ray = factory.createLineString(new Coordinate[] { polygonVertex, basePoint });
		return polygon.covers(ray);
	}

}
