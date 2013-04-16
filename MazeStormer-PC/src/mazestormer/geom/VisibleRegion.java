package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.math.DoubleMath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

public class VisibleRegion extends PointVisibility {

	protected final Geometry obstacles;
	protected final Polygon subject;

	public VisibleRegion(Geometry obstacles, Polygon subject, Coordinate viewCoord) throws NullPointerException {
		super(checkNotNull(obstacles).getFactory(), checkNotNull(viewCoord));
		this.obstacles = checkNotNull(obstacles);
		this.subject = checkNotNull(subject);
	}

	public static Geometry build(Geometry obstacles, Polygon subject, Coordinate viewCoord) {
		return new VisibleRegion(obstacles, subject, viewCoord).build();
	}

	public Geometry build() {
		// Get size needed for colliding regions to pass through all geometry
		double collisionSize = getCollisionSize(subject);

		// Collect line segments
		List<LineSegment> segments = collect(subject);

		// Find colliding regions
		Collection<Geometry> collidingRegions = getCollidingRegions(segments, collisionSize);

		// Combine and produce result
		Geometry colliding = combine(collidingRegions);
		return produceResult(colliding);
	}

	/**
	 * Get the size needed for colliding regions to pass through the subject.
	 * 
	 * @param subject
	 *            The subject.
	 * @return The required size of colliding regions.
	 */
	private double getCollisionSize(Polygon subject) {
		// Get envelope of complete geometry
		Envelope envelope = subject.getEnvelopeInternal();
		envelope.expandToInclude(viewCoord);
		// Return length of diagonal
		double w = envelope.getWidth(), h = envelope.getHeight();
		return Math.sqrt(w * w + h * h);
	}

	protected Collection<Geometry> getCollidingRegions(List<LineSegment> edges, double collisionSize) {
		List<Geometry> regions = new LinkedList<Geometry>();
		getCollidingRegions(edges, regions, collisionSize);
		return regions;
	}

	protected final void getCollidingRegions(List<LineSegment> edges, Collection<Geometry> regions, double collisionSize) {
		for (LineSegment segment : edges) {
			regions.addAll(getCollidingRegions(segment, collisionSize));
		}
	}

	// TODO Change to visible regions again?
	protected Collection<Geometry> getCollidingRegions(LineSegment screen, double collisionSize) {
		// Find invisible segment parts
		Geometry blocked = getBlockedSegments(obstacles, screen);
		// Exit if empty
		if (blocked.isEmpty()) {
			return Collections.emptySet();
		}
		// Build triangles
		return buildCollidingTriangles(blocked, collisionSize);
	}

	/**
	 * Get the blocked segments of the screen.
	 * 
	 * @param screen
	 *            The line segment on which to project.
	 * @return The segments invisible from the view point and the edge.
	 */
	protected Geometry getBlockedSegments(Geometry obstacles, LineSegment screen) {
		// Start with all points between view point and screen
		Geometry view = getViewingTriangle(screen);
		// Find collisions with polygon
		Geometry collisions = view.intersection(obstacles);
		// Exit if no collisions
		if (collisions.isEmpty()) {
			return GeometryUtils.emptyPolygon(factory);
		}
		// Return blocked segments
		Geometry screenGeom = screen.toGeometry(factory);
		Geometry blocked = getProjections(collisions, screen);
		return screenGeom.intersection(blocked);
	}

	protected Collection<Geometry> buildCollidingTriangles(Geometry lineStrings, double size) {
		int numGeometries = lineStrings.getNumGeometries();
		List<Geometry> triangles = new ArrayList<Geometry>(numGeometries);
		for (int i = 0; i < numGeometries; ++i) {
			Geometry line = lineStrings.getGeometryN(i);
			// TODO What to do with point intersections?
			if (line.getDimension() < 1) {
				continue;
			}
			// Add colliding triangle
			Coordinate[] coords = line.getCoordinates();
			assert (coords.length == 2);
			Polygon triangle = getCollidingTriangle(coords[0], coords[1], size);
			// Ignore too small triangles
			if (!DoubleMath.fuzzyEquals(triangle.getArea(), 0d, TOLERANCE)) {
				triangles.add(triangle);
			}
		}
		return triangles;
	}

	protected Polygon getCollidingTriangle(Coordinate leftVertex, Coordinate rightVertex, double size) {
		// Scale left vertex
		LineSegment leftSegment = new LineSegment(viewCoord, leftVertex);
		leftVertex = leftSegment.pointAlong(size / leftSegment.getLength());
		// Scale right vertex
		LineSegment rightSegment = new LineSegment(viewCoord, rightVertex);
		rightVertex = rightSegment.pointAlong(size / rightSegment.getLength());
		// Return triangle
		return getViewingTriangle(leftVertex, rightVertex);
	}

	private Geometry combine(Collection<Geometry> regions) {
		// Union all regions
		Geometry result = factory.createGeometryCollection(GeometryFactory.toGeometryArray(regions));
		result = result.union();
		return result;
	}

	private Geometry produceResult(Geometry colliding) {
		// Return non-obscured portions of subject
		return subject.difference(colliding);
	}

}
