package mazestormer.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class ParallelVisibilityPolygon extends VisibilityPolygon {

	private static final int THRESHOLD = 16;

	public ParallelVisibilityPolygon(Polygon polygon, Coordinate viewCoord) throws IllegalArgumentException {
		super(polygon, viewCoord);
	}

	@Override
	public Geometry build() {

		/*
		 * 1. Create line segments
		 */
		List<LineSegment> segments = new ArrayList<LineSegment>(polygon.getNumPoints());
		// Exterior shell
		segments.addAll(collect(polygon.getExteriorRing()));
		// Interior holes
		for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
			segments.addAll(collect(polygon.getInteriorRingN(i)));
		}

		/*
		 * 2. Execute projections in parallel
		 */
		Collection<Geometry> regions = new ConcurrentLinkedQueue<Geometry>();
		Task task = new Task(segments, regions);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(task);

		/*
		 * 3. Combine and simplify
		 */
		Geometry result = factory.buildGeometry(regions);
		result = GeometryUtils.removeCollinear(result);
		return result;
	}

	protected List<LineSegment> collect(LinearRing ring) {
		Coordinate[] coords = ring.getCoordinates();
		// The first and last vertices are equal in a closed ring
		// so make sure to not treat those as an edge
		int limit = coords.length - 1;
		List<LineSegment> segments = new ArrayList<LineSegment>(limit);
		for (int i = 0; i < limit; ++i) {
			// i = vertex index
			// j = next vertex index
			final int j = (i + 1) % coords.length;
			LineSegment edge = new LineSegment(coords[i], coords[j]);
			segments.add(edge);
		}
		return segments;
	}

	protected List<LineSegment> collect(LineString ring) {
		return collect(toLinearRing(ring));
	}

	private class Task extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		private final List<LineSegment> input;
		private final Collection<Geometry> output;

		public Task(List<LineSegment> input, Collection<Geometry> output) {
			this.input = input;
			this.output = output;
		}

		private void computeDirectly() {
			for (LineSegment segment : input) {
				output.addAll(getVisibleRegions(segment));
			}
		}

		@Override
		protected void compute() {
			final int size = input.size();
			if (size < THRESHOLD) {
				computeDirectly();
			} else {
				int mid = size >>> 2;
				invokeAll(new Task(input.subList(0, mid), output), new Task(input.subList(mid, size), output));
			}
		}
	}

}
