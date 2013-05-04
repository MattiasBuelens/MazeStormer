package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

public final class GeometryUtils {

	private GeometryUtils() {
	}

	public static Point2D fromCoordinate(Coordinate coord) {
		checkNotNull(coord);

		double x = coord.getOrdinate(Coordinate.X);
		double y = coord.getOrdinate(Coordinate.Y);
		return new Point2D.Double(x, y);
	}

	public static Coordinate toCoordinate(Point2D point) {
		checkNotNull(point);

		return new Coordinate(point.getX(), point.getY());
	}

	public static Point toGeometry(Point2D point, GeometryFactory geomFact) {
		checkNotNull(point);
		checkNotNull(geomFact);

		return geomFact.createPoint(toCoordinate(point));
	}

	public static LineString toGeometry(Line2D line, GeometryFactory geomFact) {
		checkNotNull(line);
		checkNotNull(geomFact);

		final Coordinate[] coords = new Coordinate[2];
		coords[0] = toCoordinate(line.getP1());
		coords[1] = toCoordinate(line.getP2());

		return geomFact.createLineString(coords);
	}

	public static Polygon toGeometry(Rectangle2D rect, GeometryFactory geomFact) {
		checkNotNull(rect);
		checkNotNull(geomFact);

		final double x = rect.getX(), y = rect.getY(), w = rect.getWidth(), h = rect.getHeight();
		final Coordinate[] coords = new Coordinate[5];
		coords[0] = new Coordinate(x, y);
		coords[1] = new Coordinate(x + w, y);
		coords[2] = new Coordinate(x + w, y + h);
		coords[3] = new Coordinate(x, y + h);
		coords[4] = coords[0];

		return geomFact.createPolygon(coords);
	}

	public static Polygon removeCollinear(Polygon polygon) {
		return (Polygon) removeCollinearGeometry(polygon);
	}

	public static Geometry removeCollinear(Geometry geometry) {
		return removeCollinearGeometry(geometry);
	}

	private static Geometry removeCollinearGeometry(Geometry geometry) {
		return DouglasPeuckerSimplifier.simplify(geometry, 0d);
	}

	public static Polygon emptyPolygon(GeometryFactory geomFact) {
		return geomFact.createPolygon(null, null);
	}

	public static Polygon copy(Polygon polygon, GeometryFactory geomFact) {
		LinearRing shell = (LinearRing) polygon.getExteriorRing().clone();
		LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
		for (int i = 0; i < holes.length; ++i) {
			holes[i] = (LinearRing) polygon.getInteriorRingN(i).clone();
		}
		return geomFact.createPolygon(shell, holes);
	}

}
