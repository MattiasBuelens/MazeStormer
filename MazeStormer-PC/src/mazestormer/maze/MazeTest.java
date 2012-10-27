package mazestormer.maze;

import static org.junit.Assert.*;
import lejos.geom.Point;

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
		Point position = new Point(2, 3);
		Point neighbor = orientation.shift(position);
		
		Edge edge = new Edge(position, orientation);
		Tile tile = maze.getTileAt(position);
		
		maze.addEdge(edge);
		
		assertTrue(tile.hasEdge(edge));
		assertTrue(edge.touches(position));
		assertTrue(edge.touches(neighbor));
	}

}
