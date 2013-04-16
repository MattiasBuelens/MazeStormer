package mazestormer.geom;

import java.util.concurrent.TimeUnit;

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
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class VisibleRegionTest {

	private static final IMaze maze = new Maze();
	private static Geometry walls;

	private Polygon subject;
	private final Stopwatch stopwatch = new Stopwatch();

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
		VisibleRegionTest test = new VisibleRegionTest();
		test.createSubject();
		test.fullyVisible();
		test.partiallyVisible();
		test.invisible();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Parse maze
		String mazeFilePath = VisibilityPolygonTest.class.getResource("/res/mazes/Semester2_Demo2.txt").getPath();
		new Parser(maze).parse(FileUtils.load(mazeFilePath));
		// Walls
		walls = maze.getEdgeGeometry();
		System.out.println("Maze walls:");
		System.out.println(walls.toText());
		System.out.println();
	}

	@Before
	public void createSubject() {
		GeometricShapeFactory factory = new GeometricShapeFactory();
		factory.setNumPoints(30);
		factory.setCentre(toCoordinate(new LongPoint(3, 5)));
		factory.setSize(30);
		factory.setRotation(Math.PI / 4d);
		subject = factory.createRectangle();
	}

	@Test
	public void fullyVisible() {
		test("sequential full", new LongPoint(4, 5), false);
		test("parallel full", new LongPoint(4, 5), true);
	}

	@Test
	public void partiallyVisible() {
		test("sequential partial", new LongPoint(4, 4), false);
		test("parallel partial", new LongPoint(4, 4), true);
	}

	@Test
	public void invisible() {
		test("sequential invisible", new LongPoint(5, 5), false);
		test("parallel invisible", new LongPoint(5, 5), true);
	}

	private void test(String name, LongPoint viewPoint, boolean isParallel) {
		Geometry visibleSubject;
		Coordinate viewCoord = toCoordinate(viewPoint);

		start();
		if (isParallel) {
			visibleSubject = ParallelVisibleRegion.build(walls, subject, viewCoord);
		} else {
			visibleSubject = VisibleRegion.build(walls, subject, viewCoord);
		}
		visibleSubject = GeometryPrecisionReducer.reduce(visibleSubject, new PrecisionModel(100d));
		System.out.println(name + ":\t" + visibleSubject.toText());
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

	public static Coordinate toCoordinate(LongPoint tilePosition) {
		return GeometryUtils.toCoordinate(maze.getTileCenter(tilePosition));
	}

}
