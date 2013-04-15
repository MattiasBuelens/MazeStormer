package mazestormer.geom;

import java.util.concurrent.TimeUnit;

import lejos.geom.Point;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.parser.FileUtils;
import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class VisibilityPolygonTest {

	private static final IMaze maze = new Maze();

	private Polygon inner;
	private Coordinate viewCoord;

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		VisibilityPolygonTest test = new VisibilityPolygonTest();
		test.getSurroundingGeometry();
		test.sequentialVisibilityPolygon();
		test.parallelVisibilityPolygon();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String mazeFilePath = VisibilityPolygonTest.class.getResource("/res/mazes/Semester2_Demo2.txt").getPath();
		new Parser(maze).parse(FileUtils.load(mazeFilePath));
	}

	@Before
	public void getSurroundingGeometry() {
		final LongPoint tilePoint = new LongPoint(3, 5);

		Point viewPoint = maze.getTileCenter(tilePoint);
		viewCoord = GeometryUtils.toCoordinate(viewPoint);
		inner = maze.getSurroundingGeometry(viewPoint);

		System.out.println(inner.toText());
	}

	@Test
	public void sequentialVisibilityPolygon() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Geometry visPoly = new VisibilityPolygon(inner, viewCoord).build();
		stopwatch.stop();

		System.out.println(visPoly.toText());
		System.out.println("Sequential: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
	}

	@Test
	public void parallelVisibilityPolygon() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		Geometry visPoly = new ParallelVisibilityPolygon(inner, viewCoord).build();
		stopwatch.stop();

		System.out.println(visPoly.toText());
		System.out.println("Parallel: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
	}

}
