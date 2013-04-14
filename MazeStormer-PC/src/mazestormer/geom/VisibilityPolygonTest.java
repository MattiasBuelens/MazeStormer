package mazestormer.geom;

import lejos.geom.Point;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class VisibilityPolygonTest {

	private static final IMaze maze = new Maze();

	private Polygon inner;
	private Coordinate viewCoord;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String mazeFilePath = VisibilityPolygonTest.class.getResource("res/mazes/Semester2_Demo2.txt").getPath();
		new Parser(maze).parse(FileUtils.load(mazeFilePath));
	}

	@Before
	public void getSurroundingGeometry() {
		LongPoint tilePoint = new LongPoint(3, 5);
		Point viewPoint = maze.getTileCenter(tilePoint);

		viewCoord = GeometryUtils.toCoordinate(viewPoint);
		inner = maze.getSurroundingGeometry(viewPoint);

		System.out.println(inner.toText());
	}

	@Test
	public void visibilityPolygon() {
		Geometry visPoly = new VisibilityPolygon(inner, viewCoord).build();

		System.out.println(visPoly.toText());
	}

}
