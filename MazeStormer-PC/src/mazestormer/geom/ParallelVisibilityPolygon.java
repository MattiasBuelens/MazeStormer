package mazestormer.geom;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

public class ParallelVisibilityPolygon extends VisibilityPolygon {

	private static final int THRESHOLD = 8;
	private static final ForkJoinPool POOL = new ForkJoinPool();

	public ParallelVisibilityPolygon(Polygon polygon, Coordinate viewCoord) throws NullPointerException {
		super(polygon, viewCoord);
	}

	public static Geometry build(Polygon polygon, Coordinate viewCoord) {
		return new ParallelVisibilityPolygon(polygon, viewCoord).build();
	}

	@Override
	protected Collection<Geometry> getVisibleRegions(List<LineSegment> edges) {
		Collection<Geometry> regions = new ConcurrentLinkedQueue<Geometry>();
		Task task = new Task(edges, regions);
		POOL.invoke(task);
		return regions;
	}

	private class Task extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		private final List<LineSegment> input;
		private final Collection<Geometry> output;

		public Task(List<LineSegment> input, Collection<Geometry> output) {
			this.input = input;
			this.output = output;
		}

		@Override
		protected void compute() {
			final int size = input.size();
			if (size <= THRESHOLD) {
				// Compute directly
				getVisibleRegions(input, output);
			} else {
				// Split up task
				int mid = size >>> 2;
				Task leftTask = new Task(input.subList(0, mid), output);
				Task rightTask = new Task(input.subList(mid, size), output);
				invokeAll(leftTask, rightTask);
			}
		}
	}

}
