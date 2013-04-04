package mazestormer.maze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CombinedMazeTest {

	// @formatter:off
	private static String smallMazeSource = "4 4\n"
			+ "DeadEnd.W.V Straight.W.01 Corner.E DeadEnd.N.V\n"
			+ "Corner.N Straight.W.S1E T.E.S2S Straight.N.02\n"
			+ "Straight.S.00 Corner.N.S4N T.S.S3W Corner.S\n"
			+ "DeadEnd.S.V Corner.W Straight.E.03 DeadEnd.E.V";
	// @formatter:on

	private static Maze smallMaze;

	private Maze ownDiscoveredMaze = new Maze();
	private LongPoint[] ownDiscoveredPoints = { new LongPoint(0, 3), new LongPoint(1, 3), new LongPoint(2, 3),
			new LongPoint(3, 3), new LongPoint(0, 2), new LongPoint(1, 2), new LongPoint(2, 2), new LongPoint(3, 2),
			new LongPoint(0, 1), new LongPoint(2, 1), new LongPoint(3, 1), new LongPoint(0, 0) };
	private TileTransform ownTileTransform = new TileTransform(new LongPoint(1, 2), 3);

	private Maze partnersDiscoveredMaze = new Maze();
	private LongPoint[] partnerDiscoveredPoints = { new LongPoint(0, 3), new LongPoint(1, 3), new LongPoint(2, 3),
			new LongPoint(3, 3), new LongPoint(2, 2), new LongPoint(3, 2), new LongPoint(1, 1), new LongPoint(2, 1),
			new LongPoint(3, 1), new LongPoint(1, 0), new LongPoint(2, 0), new LongPoint(3, 0) };
	private TileTransform partnersTileTransform = new TileTransform(new LongPoint(2, 2), 2);

	private CombinedMaze combinedMaze;

	@BeforeClass
	public static void setUpBeforeClass() throws ParseException {
		smallMaze = parse(smallMazeSource);
	}

	@Before
	public void setUp() {
		combinedMaze = new CombinedMaze(ownDiscoveredMaze);
		combinedMaze.setPartnerMaze(partnersDiscoveredMaze);
	}

	@After
	public void tearDown() {
		ownDiscoveredMaze.clear();
		partnersDiscoveredMaze.clear();
		combinedMaze.clear();
	}

	@Test
	public void testOwnDiscoveredMaze() {
		// Run test
		for (LongPoint lp : ownDiscoveredPoints) {
			combinedMaze.importTile(smallMaze.getTileAt(lp), ownTileTransform);
		}

		for (LongPoint lp : partnerDiscoveredPoints) {
			partnersDiscoveredMaze.importTile(smallMaze.getTileAt(lp), partnersTileTransform);
		}

		System.out.println(Parser.stringify(smallMaze) + "END");
		System.out.println(Parser.stringify(ownDiscoveredMaze));

		// Test results
		assertTrue(ownDiscoveredMaze.getTileAt(new LongPoint(0, 1)).hasBarcode());
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(0, 1)).getBarcode().getValue(), (byte) 1);

		assertTrue(ownDiscoveredMaze.getTileAt(new LongPoint(2, 0)).hasBarcode());
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(2, 0)).getBarcode().getValue(), (byte) 2);
	}

	/**
	 * Parse the given source into a new maze.
	 */
	private static Maze parse(CharSequence source) throws ParseException {
		Maze maze = new Maze();
		Parser parser = new Parser(maze);
		parser.parse(source);
		return maze;
	}

}
