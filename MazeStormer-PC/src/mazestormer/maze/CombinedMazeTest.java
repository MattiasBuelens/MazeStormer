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

//	private static String largeMazeSource = "10 8"
//			+ "Cross.N T.E Closed DeadEnd.N.V T.W T.E DeadEnd.N.S1E DeadEnd.N.V T.W Cross.N"
//			+ "Cross.N T.E Closed Straight.N.04 Corner.W Corner.S Straight.N Straight.N.01 T.W Cross.N"
//			+ "Cross.N T.E Corner.N.S3N Cross.N Corner.E Corner.N Cross.N T.E T.W Cross.N"
//			+ "Cross.N T.E DeadEnd.S Straight.N.11 T.W T.E Straight.N.17 DeadEnd.S T.W Cross.N"
//			+ "Cross.N T.E Closed Seesaw.N DeadEnd.S Straight.N.55 Seesaw.N Closed T.W Cross.N"
//			+ "T.S Corner.S Closed Seesaw.S Corner.N Corner.S Seesaw.S Closed Corner.W T.S"
//			+ "DeadEnd.W.V Straight.W.03 Corner.E.S4N Straight.N.13 T.W Corner.E Straight.N.15 Corner.N Straight.E DeadEnd.E"
//			+ "DeadEnd.W DeadEnd.E Corner.W T.S Corner.S Corner.W  T.S.S2W T.S Straight.E.06 DeadEnd.E.V";
//	private static Maze largeMaze = new Maze();
//	private static Parser largeMazeParser = new Parser(largeMaze);

	private static String smallMazeSource = "4 4\n"
			+ "DeadEnd.W.V Straight.W.01 Corner.E DeadEnd.N.V\n"
			+ "Corner.N Straight.W.S1E T.E.S2S Straight.N.02\n"
			+ "Straight.S.00 Corner.N.S4N T.S.S3W Corner.S\n"
			+ "DeadEnd.S.V Corner.W Straight.E.03 DeadEnd.E.V";
	private static Maze smallMaze = new Maze();
	private static Parser smallMazeParser = new Parser(smallMaze);

	private static Maze ownDiscoveredMaze = new Maze();
	private static LongPoint[] ownDiscoveredPoints = {new LongPoint(0,3), new LongPoint(1,3), new LongPoint(2,3), new LongPoint(3,3), new LongPoint(0, 2), new LongPoint(1,2), new LongPoint(2,2), new LongPoint(3,2), new LongPoint(0,1), new LongPoint(2,1), new LongPoint(3,1), new LongPoint(0,0)};;
	private static TileTransform ownTileTransform = new TileTransform(new LongPoint(1,2), 3);

	private static Maze partnersDiscoveredMaze = new Maze();
	private static LongPoint[] partnerDiscoveredPoints = {new LongPoint(0,3), new LongPoint(1,3), new LongPoint(2,3), new LongPoint(3,3), new LongPoint(2,2), new LongPoint(3,2), new LongPoint(1,1), new LongPoint(2,1), new LongPoint(3,1), new LongPoint(1,0), new LongPoint(2,0), new LongPoint(3,0)};;
	private static TileTransform partnersTileTransform = new TileTransform(new LongPoint(2,2), 2);
	
	private static CombinedMaze combinedMaze;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
//			largeMazeParser.parse(largeMazeSource);
			smallMazeParser.parse(smallMazeSource);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ownDiscoveredMaze.setOrigin(new Pose(60f, 100f, 0f));
		for(LongPoint lp : ownDiscoveredPoints) {
			ownDiscoveredMaze.importTile(smallMaze.getTileAt(lp), ownTileTransform);
		}
		
		partnersDiscoveredMaze.setOrigin(new Pose(100f, 100f, -90f));
		for(LongPoint lp : partnerDiscoveredPoints) {
			partnersDiscoveredMaze.importTile(smallMaze.getTileAt(lp), partnersTileTransform);
		}
		
		combinedMaze = new CombinedMaze(ownDiscoveredMaze);
		combinedMaze.setPartnerMaze(partnersDiscoveredMaze);
		
		System.out.println(ownDiscoveredMaze.getTiles());
	}

	@Before
	public void setUp() {

	}
	
	@Test
	public void testOwnDiscoveredMaze(){
		assertTrue(ownDiscoveredMaze.getTileAt(new LongPoint(0,1)).hasBarcode() && ownDiscoveredMaze.getTileAt(new LongPoint(2,0)).hasBarcode());
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(0,1)).getBarcode().getValue(), (byte) 1);
		assertEquals(ownDiscoveredMaze.getTileAt(new LongPoint(2,0)).getBarcode().getValue(), (byte) 2);
	}
	
	

}
