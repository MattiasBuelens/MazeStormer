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

	private final Stopwatch stopwatch = new Stopwatch();

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
		start();
		Geometry visPoly = VisibilityPolygon.build(inner, viewCoord);
		System.out.println(visPoly.toText());
		System.out.print("Sequential: ");
		stop();
	}

	@Test
	public void parallelVisibilityPolygon() {
		start();
		Geometry visPoly = ParallelVisibilityPolygon.build(inner, viewCoord);
		System.out.println(visPoly.toText());
		System.out.print("Parallel: ");
		stop();
	}

	private void start() {
		stopwatch.reset();
		stopwatch.start();
	}

	private void stop() {
		stopwatch.stop();
		System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
	}

}
