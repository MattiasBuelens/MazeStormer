package mazestormer.maze;

import static org.junit.Assert.*;
import lejos.geom.Point;

import org.junit.Test;

public class EdgeTest {

	@Test
	public void touches() {
		Orientation orientation = Orientation.NORTH;
		Point position = new Point(2, 3);
		Point neighbor = orientation.shift(position);
		Point notTouching = new Point(5, 5);

		Edge edge = new Edge(position, orientation);

		assertTrue(edge.touches(position));
		assertTrue(edge.touches(neighbor));

		assertFalse(edge.touches(notTouching));
	}

	@Test
	public void getOrientationFrom() {
		Orientation orientation = Orientation.NORTH;
		Point position = new Point(2, 3);
		Point neighbor = orientation.shift(position);

		Edge edge = new Edge(position, orientation);

		assertEquals(Orientation.NORTH, edge.getOrientationFrom(position));
		assertEquals(Orientation.SOUTH, edge.getOrientationFrom(neighbor));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getOrientationFrom_NotTouching() {
		Orientation orientation = Orientation.NORTH;
		Point position = new Point(2, 3);
		Point notTouching = new Point(5, 5);

		Edge edge = new Edge(position, orientation);

		edge.getOrientationFrom(notTouching);
		fail();
	}

}
