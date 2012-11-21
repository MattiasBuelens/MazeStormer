package mazestormer.maze;

import static org.junit.Assert.*;
import mazestormer.util.LongPoint;

import org.junit.Before;
import org.junit.Test;

public class MazeTest {

	private Maze maze;

	@Before
	public void setup() {
		maze = new Maze();
	}

	@Test
	public void addEdge() {
		Orientation orientation = Orientation.NORTH;
		LongPoint position = new LongPoint(2, 3);
		LongPoint neighbor = orientation.shift(position);

		Edge edge = new Edge(position, orientation);
		Tile tile = maze.getTileAt(position);

		//maze.addWall(edge);

		assertTrue(tile.hasEdge(edge));
		assertTrue(edge.touches(position));
		assertTrue(edge.touches(neighbor));
	}

}
