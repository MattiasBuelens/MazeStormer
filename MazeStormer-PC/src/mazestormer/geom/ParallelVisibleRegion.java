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

public class ParallelVisibleRegion extends VisibleRegion {

	private static final int THRESHOLD = 8;

	public ParallelVisibleRegion(Geometry obstacles, Polygon subject, Coordinate viewCoord)
			throws IllegalArgumentException {
		super(obstacles, subject, viewCoord);
	}

	public static Geometry build(Geometry obstacles, Polygon subject, Coordinate viewCoord) {
		return new ParallelVisibleRegion(obstacles, subject, viewCoord).build();
	}

	@Override
	protected Collection<Geometry> getCollidingRegions(List<LineSegment> edges, double collisionSize) {
		Collection<Geometry> regions = new ConcurrentLinkedQueue<Geometry>();
		Task task = new Task(edges, regions, collisionSize);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(task);
		return regions;
	}

	private class Task extends RecursiveAction {

		private static final long serialVersionUID = 1L;

		private final List<LineSegment> input;
		private final Collection<Geometry> output;

		private final double collisionSize;

		public Task(List<LineSegment> input, Collection<Geometry> output, double collisionSize) {
			this.input = input;
			this.output = output;
			this.collisionSize = collisionSize;
		}

		@Override
		protected void compute() {
			final int size = input.size();
			if (size <= THRESHOLD) {
				// Compute directly
				getCollidingRegions(input, output, collisionSize);
			} else {
				// Split up task
				int mid = size >>> 2;
				Task leftTask = new Task(input.subList(0, mid), output, collisionSize);
				Task rightTask = new Task(input.subList(mid, size), output, collisionSize);
				invokeAll(leftTask, rightTask);
			}
		}
	}

}
