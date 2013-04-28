package mazestormer.geom;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class MultiFractionSegment {

	private final List<FractionSegment> segments = new ArrayList<FractionSegment>();
	private static final Comparator<FractionSegment> comparator = new LeftBoundComparator();

	public MultiFractionSegment() {
	}

	public MultiFractionSegment(Collection<FractionSegment> segments) {
		addSegments(checkNotNull(segments));
	}

	public List<FractionSegment> getSegments() {
		return Collections.unmodifiableList(segments);
	}

	public void addSegment(FractionSegment segment) {
		checkNotNull(segment);
		// Get sorted index
		int index = Collections.binarySearch(segments, segment, comparator);
		// If element does not already exists in list, get the insertion point
		if (index < 0) {
			index = -index - 1;
		}
		segments.add(index, segment);
	}

	public void addSegments(Collection<? extends FractionSegment> segments) {
		for (FractionSegment segment : segments) {
			addSegment(segment);
		}
	}

	public MultiFractionSegment combine() {
		List<FractionSegment> combined = new ArrayList<FractionSegment>();
		Iterator<FractionSegment> it = getSegments().listIterator();
		if (!it.hasNext()) {
			return new MultiFractionSegment();
		}

		/*
		 * http://stackoverflow.com/a/5314751/1321716
		 */
		FractionSegment previous = it.next();
		while (it.hasNext()) {
			FractionSegment current = it.next();
			if (current.left() <= previous.right()) {
				previous = previous.merge(current);
			} else {
				combined.add(previous);
				previous = current;
			}
		}
		combined.add(previous);

		return new MultiFractionSegment(combined);
	}

	public List<LineSegment> segmentsAlong(LineSegment lineSegment) {
		List<LineSegment> lineSegments = new ArrayList<LineSegment>();
		for (FractionSegment segment : getSegments()) {
			Coordinate left = lineSegment.pointAlong(segment.left());
			Coordinate right = lineSegment.pointAlong(segment.right());
			lineSegments.add(new LineSegment(left, right));
		}
		return lineSegments;
	}

	@Override
	public String toString() {
		return "MultiFractionSegment [" + Joiner.on(", ").join(getSegments()) + "]";
	}

	private static class LeftBoundComparator implements Comparator<FractionSegment> {

		@Override
		public int compare(FractionSegment a, FractionSegment b) {
			return Double.compare(a.left(), b.left());
		}

	}

}
