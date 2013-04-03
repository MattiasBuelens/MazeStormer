package mazestormer.maze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import lejos.robotics.navigation.Pose;
import mazestormer.maze.parser.Parser;
import mazestormer.util.LongPoint;

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

	private static Maze smallMaze = new Maze();
	private static Parser smallMazeParser = new Parser(smallMaze);

	private static Maze ownDiscoveredMaze = new Maze();
	private static LongPoint[] ownDiscoveredPoints = { new LongPoint(0, 3), new LongPoint(1, 3), new LongPoint(2, 3),
			new LongPoint(3, 3), new LongPoint(0, 2), new LongPoint(1, 2), new LongPoint(2, 2), new LongPoint(3, 2),
			new LongPoint(0, 1), new LongPoint(2, 1), new LongPoint(3, 1), new LongPoint(0, 0) };
	private static TileTransform ownTileTransform = new TileTransform(new LongPoint(1, 2), 3);

	private static Maze partnersDiscoveredMaze = new Maze();
	private static LongPoint[] partnerDiscoveredPoints = { new LongPoint(0, 3), new LongPoint(1, 3),
			new LongPoint(2, 3), new LongPoint(3, 3), new LongPoint(2, 2), new LongPoint(3, 2), new LongPoint(1, 1),
			new LongPoint(2, 1), new LongPoint(3, 1), new LongPoint(1, 0), new LongPoint(2, 0), new LongPoint(3, 0) };
	private static TileTransform partnersTileTransform = new TileTransform(new LongPoint(2, 2), 2);

	private static CombinedMaze combinedMaze;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			smallMazeParser.parse(smallMazeSource);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ownDiscoveredMaze.setOrigin(new Pose(60f, 100f, 3));
		for(LongPoint lp : ownDiscoveredPoints) {
			ownDiscoveredMaze.importTile(smallMaze.getTileAt(lp), ownTileTransform);
		}
		for (LongPoint lp : partnerDiscoveredPoints) {
			partnersDiscoveredMaze.importTile(smallMaze.getTileAt(lp), partnersTileTransform);
		}
		
//		combinedMaze = new CombinedMaze(ownDiscoveredMaze);
//		combinedMaze.setPartnerMaze(partnersDiscoveredMaze);
		
		for(Tile tile : smallMaze.getTiles()) {
			System.out.println(tile.getPosition());
			if(tile.hasBarcode()) System.out.println(tile.getBarcode().getValue());
		}
		
		for(Tile tile : ownDiscoveredMaze.getTiles()) {
			System.out.println(tile.getPosition());
			if(tile.hasBarcode()) System.out.println(tile.getBarcode().getValue());
		}

		combinedMaze = new CombinedMaze(ownDiscoveredMaze);
		combinedMaze.setPartnerMaze(partnersDiscoveredMaze);
	}

	@Before
	public void setUp() {

	}

	@Test
	public void testOwnDiscoveredMaze() {
		assertTrue(ownDiscoveredMaze.getTileAt(new LongPoint(0, 1)).hasBarcode());
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(0, 1)).getBarcode().getValue(), (byte) 1);

		assertTrue(ownDiscoveredMaze.getTileAt(new LongPoint(2, 0)).hasBarcode());
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(2, 0)).getBarcode().getValue(), (byte) 2);
	}

}
