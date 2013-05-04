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
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

public class VisibleRegion extends PointVisibility {

	protected final Geometry walls;
	protected final Polygon subject;

	public VisibleRegion(Geometry walls, Polygon subject, Coordinate viewCoord) throws NullPointerException {
		super(checkNotNull(walls).getFactory(), checkNotNull(viewCoord));
		this.walls = checkNotNull(walls);
		this.subject = checkNotNull(subject);
	}

	public static Geometry build(Geometry walls, Polygon subject, Coordinate viewCoord) {
		return new VisibleRegion(walls, subject, viewCoord).build();
	}

	public Geometry build() {
		// Get size needed for visible regions to pass through all geometry
		double collisionSize = getCollisionSize(subject);

		// Collect line segments
		List<LineSegment> segments = collect(subject);

		// Find blocked regions
		Collection<Geometry> blockedRegions = getBlockedRegions(segments, collisionSize);

		// Combine and produce result
		Geometry blocked = combine(blockedRegions);
		return produceResult(blocked);
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

	protected Collection<Geometry> getBlockedRegions(List<LineSegment> edges, double collisionSize) {
		List<Geometry> regions = new LinkedList<Geometry>();
		getBlockedRegions(edges, regions, collisionSize);
		return regions;
	}

	protected final void getBlockedRegions(List<LineSegment> edges, Collection<Geometry> regions, double collisionSize) {
		for (LineSegment segment : edges) {
			regions.addAll(getBlockedRegions(segment, collisionSize));
		}
	}

	protected Collection<Geometry> getBlockedRegions(LineSegment screen, double collisionSize) {
		// Find invisible segment parts
		MultiLineString blocked = getBlockedSegments(screen);
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
	protected MultiLineString getBlockedSegments(LineSegment screen) {
		// Start with all points between view point and screen
		Geometry view = getViewingTriangle(screen);
		// Find collisions with polygon
		Geometry collisions = view.intersection(walls);
		// Exit if no collisions
		if (collisions.isEmpty()) {
			return factory.createMultiLineString(null);
		}
		// Get and process projections
		MultiLineString blocked = getProjections(collisions, screen);
		return reduceMultiLineString(blocked);
	}

	protected Collection<Geometry> buildCollidingTriangles(MultiLineString lineStrings, double size) {
		int numGeometries = lineStrings.getNumGeometries();
		List<Geometry> triangles = new ArrayList<Geometry>(numGeometries);
		for (int i = 0; i < numGeometries; ++i) {
			Geometry line = lineStrings.getGeometryN(i);
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
		// Combine regions
		Geometry result = factory.buildGeometry(regions);
		result = GeometryUtils.removeCollinear(result);
		return result;
	}

	private Geometry produceResult(Geometry blocked) {
		// Return non-obscured portions of subject
		Geometry result = subject.difference(blocked);
		result = GeometryPrecisionReducer.reduce(result, new PrecisionModel(1e3));
		return result;
	}

}
