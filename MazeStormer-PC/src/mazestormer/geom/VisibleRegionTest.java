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
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class VisibleRegionTest {

	private static final IMaze maze = new Maze();
	private static Geometry obstacles;

	private Polygon subject;
	private final Stopwatch stopwatch = new Stopwatch();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Parse maze
		String mazeFilePath = VisibilityPolygonTest.class.getResource("/res/mazes/Semester2_Demo2.txt").getPath();
		new Parser(maze).parse(FileUtils.load(mazeFilePath));
		// Get edge geometry
		obstacles = maze.getEdgeGeometry();
		System.out.println(obstacles.toText());
	}

	@Before
	public void createSubject() {
		GeometricShapeFactory factory = new GeometricShapeFactory(obstacles.getFactory());
		factory.setNumPoints(4);
		factory.setCentre(toCoordinate(new LongPoint(3, 5)));
		factory.setSize(30);
		subject = factory.createRectangle();
	}

	@Test
	public void fullyVisible() {
		start();
		Coordinate viewCoord = toCoordinate(new LongPoint(4, 5));
		Geometry visibleSubject = VisibleRegion.build(obstacles, subject, viewCoord);
		System.out.println("Full: " + visibleSubject.toText());
		stop();
	}

	@Test
	public void partiallyVisible() {
		start();
		Coordinate viewCoord = toCoordinate(new LongPoint(4, 4));
		Geometry visibleSubject = VisibleRegion.build(obstacles, subject, viewCoord);
		System.out.println("Partial: " + visibleSubject.toText());
		stop();
	}

	@Test
	public void invisible() {
		start();
		Coordinate viewCoord = toCoordinate(new LongPoint(5, 5));
		Geometry visibleSubject = VisibleRegion.build(obstacles, subject, viewCoord);
		System.out.println("Invisible: " + visibleSubject.toText());
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
