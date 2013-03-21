package mazestormer.maze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import mazestormer.util.LongPoint;

import org.junit.Test;

public class EdgeTest {

	@Test
	public void touches() {
		Orientation orientation = Orientation.NORTH;
		LongPoint position = new LongPoint(2, 3);
		LongPoint neighbor = orientation.shift(position);
		LongPoint notTouching = new LongPoint(5, 5);

		Edge edge = new Edge(position, orientation);

		assertTrue(edge.touches(position));
		assertTrue(edge.touches(neighbor));

		assertFalse(edge.touches(notTouching));
	}

	@Test
	public void getOrientationFrom() {
		Orientation orientation = Orientation.NORTH;
		LongPoint position = new LongPoint(2, 3);
		LongPoint neighbor = orientation.shift(position);

		Edge edge = new Edge(position, orientation);

		assertEquals(Orientation.NORTH, edge.getOrientationFrom(position));
		assertEquals(Orientation.SOUTH, edge.getOrientationFrom(neighbor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getOrientationFrom_NotTouching() {
		Orientation orientation = Orientation.NORTH;
		LongPoint position = new LongPoint(2, 3);
		LongPoint notTouching = new LongPoint(5, 5);

		Edge edge = new Edge(position, orientation);

		edge.getOrientationFrom(notTouching);
		fail();
	}

}
