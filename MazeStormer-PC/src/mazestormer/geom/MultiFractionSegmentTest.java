package mazestormer.geom;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class MultiFractionSegmentTest {

	@Test
	public void separate() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 1), new FractionSegment(2, 3),
				new FractionSegment(4, 5));
		List<FractionSegment> expected = segments;

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void containing() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 5), new FractionSegment(1, 2));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 5));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void touching() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 2), new FractionSegment(2, 3));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 3));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void sameStart() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 3), new FractionSegment(0, 2));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 3));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void sameEnd() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 3), new FractionSegment(2, 3));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 3));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void overlapping() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 3), new FractionSegment(2, 5));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 5));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

	@Test
	public void overlappingMultiple() {
		List<FractionSegment> segments = ImmutableList.of(new FractionSegment(0, 1), new FractionSegment(2, 4),
				new FractionSegment(3, 6), new FractionSegment(7, 8));
		List<FractionSegment> expected = ImmutableList.of(new FractionSegment(0, 1), new FractionSegment(2, 6),
				new FractionSegment(7, 8));

		MultiFractionSegment mfs = new MultiFractionSegment(segments).combine();
		assertEquals(expected, mfs.getSegments());
	}

}
