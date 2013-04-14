package mazestormer.geom;

import lejos.geom.Point;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class VisibilityPolygonTest {

	private static final IMaze maze = new Maze();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ClassLoader classLoader = VisibilityPolygonTest.class.getClassLoader();
		String mazeFilePath = classLoader.getResource("res/mazes/Semester2_Demo2.txt").getPath();
		new Parser(maze).parse(FileUtils.load(mazeFilePath));
	}

	@Test
	public void test() {
		LongPoint tilePoint = new LongPoint(3, 5);
		Point viewPoint = maze.getTileCenter(tilePoint);
		Coordinate viewCoord = GeometryUtils.toCoordinate(viewPoint);

		Polygon inner = maze.getSurroundingGeometry(viewPoint);
		Geometry visPoly = new VisibilityPolygon(inner, viewCoord).build();
		System.out.println(inner.toText());
		System.out.println(visPoly.toText());
	}

}
