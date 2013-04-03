package mazestormer.maze;

import static org.junit.Assert.*;

import java.text.ParseException;

import mazestormer.maze.parser.Parser;

import org.junit.BeforeClass;
import org.junit.Test;

public class CombinedMazeTest {

	private static String sourceMazeString = "10 8"
			+ "Cross.N T.E Closed.N DeadEnd.N.V T.W T.E DeadEnd.N.S1E DeadEnd.N.V T.W Cross.N"
			+ "Cross.N T.E Closed.N Straight.N.04 Corner.W Corner.S Straight.N Straight.N.01 T.W Cross.N"
			+ "Cross.N T.E Corner.N.S3N Cross.N Corner.E Corner.N Cross.N T.E T.W Cross.N"
			+ "Cross.N T.E DeadEnd.S Straight.N.11 T.W T.E Straight.N.17 DeadEnd.S T.W Cross.N"
			+ "Cross.N T.E Closed.N Seesaw.N DeadEnd.S Straight.N.55 Seesaw.N Closed.n T.W Cross.N"
			+ "T.S Corner.S Closed.N Seesaw.S Corner.N Corner.S Seesaw.S Closed.N Corner.W T.S"
			+ "DeadEnd.W.V Straight.W.03 Corner.E.S4N Straight.N.13 T.W Corner.E Straight.N.15 Corner.N Straight.E DeadEnd.E"
			+ "DeadEnd.W DeadEnd.E Corner.W T.S Corner.S Corner.W  T.S.S2W T.S Straight.E.06 DeadEnd.E.V";
	private static Maze sourceMaze = new Maze();
	private static Parser sourceParser = new Parser(sourceMaze);
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			sourceParser.parse(sourceMazeString);
		} catch (ParseException e) {e.printStackTrace();}
	}

	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
