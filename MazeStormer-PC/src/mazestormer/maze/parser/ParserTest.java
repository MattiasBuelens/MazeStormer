package mazestormer.maze.parser;

import static mazestormer.maze.Orientation.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.EnumSet;

import lejos.geom.Point;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

import org.junit.Test;

import com.google.common.collect.Sets;

public class ParserTest {

	@Test
	public void empty() throws ParseException {
		final Orientation[] expectedEdges = new Orientation[] {};

		String source = "0 0";
		IMaze maze = parse(source);

		Tile tile = maze.getTileAt(new LongPoint(0, 0));
		checkWalls(tile, expectedEdges);
	}

	@Test
	public void single() throws ParseException {
		final Orientation[] expectedEdges = new Orientation[] { WEST, EAST };

		String source = "1 1\nStraight.N.00";
		IMaze maze = parse(source);

		Tile tile = maze.getTileAt(new LongPoint(0, 0));
		checkWalls(tile, expectedEdges);
	}

	@Test
	public void square() throws ParseException {
		String source = "2 2\nCorner.N Corner.E\nCorner.W Corner.S";
		IMaze maze = parse(source);

		// Bottom left corner
		checkWalls(maze.getTileAt(new LongPoint(0, 0)), SOUTH, WEST);
		// Bottom right corner
		checkWalls(maze.getTileAt(new LongPoint(1, 0)), SOUTH, EAST);
		// Top left corner
		checkWalls(maze.getTileAt(new LongPoint(0, 1)), NORTH, WEST);
		// Top right corner
		checkWalls(maze.getTileAt(new LongPoint(1, 1)), NORTH, EAST);
	}

	@Test
	public void horizontal() throws ParseException {
		String source = "3 1\nDeadEnd.W Straight.E.00 DeadEnd.E";
		IMaze maze = parse(source);

		// Left
		checkWalls(maze.getTileAt(new LongPoint(0, 0)), NORTH, SOUTH, WEST);
		// Center
		checkWalls(maze.getTileAt(new LongPoint(1, 0)), NORTH, SOUTH);
		// Right
		checkWalls(maze.getTileAt(new LongPoint(2, 0)), NORTH, SOUTH, EAST);
	}

	@Test
	public void vertical() throws ParseException {
		String source = "1 3\nDeadEnd.N\nStraight.N.00\nDeadEnd.S";
		IMaze maze = parse(source);

		// Top
		checkWalls(maze.getTileAt(new LongPoint(0, 2)), WEST, EAST, NORTH);
		// Middle
		checkWalls(maze.getTileAt(new LongPoint(0, 1)), WEST, EAST);
		// Bottom
		checkWalls(maze.getTileAt(new LongPoint(0, 0)), WEST, EAST, SOUTH);
	}

	@Test
	public void commentAtStart() throws ParseException {
		parse("#valid\n1 1\nCross");
	}

	@Test
	public void commentAfterDimensions() throws ParseException {
		parse("1 1\n#valid\nCross");
	}

	@Test
	public void commentBetweenRows() throws ParseException {
		parse("2 2\nCorner.N Corner.E\n#valid\nCorner.W Corner.S");
	}

	@Test
	public void commentAtEnd() throws ParseException {
		parse("2 2\nCorner.N Corner.E\nCorner.W Corner.S\n#valid");
	}

	@Test
	public void newlineAtStart() throws ParseException {
		parse("\n\n1 1\nCross");
	}

	@Test
	public void newlineBeforeTiles() throws ParseException {
		parse("1 1\n\n\nCross");
	}

	@Test
	public void newlineAtEnd() throws ParseException {
		parse("1 1\nCross\n\n");
	}

	@Test(expected = ParseException.class)
	public void emptyString() throws ParseException {
		parse("");
	}

	@Test(expected = ParseException.class)
	public void missingDimensions() throws ParseException {
		parse("DeadEnd.W DeadEnd.E");
	}

	@Test(expected = ParseException.class)
	public void missingDimension() throws ParseException {
		parse("2\nDeadEnd.W DeadEnd.E");
	}

	@Test(expected = ParseException.class)
	public void missingNewLineAfterDimensions() throws ParseException {
		parse("1 2 DeadEnd.N\nDeadEnd.S");
	}

	@Test(expected = ParseException.class)
	public void missingSpaceBetweenTiles() throws ParseException {
		String source = "2 1\nDeadEnd.WDeadEnd.E";
		parse(source);
	}

	@Test(expected = ParseException.class)
	public void tooFewRows() throws ParseException {
		parse("2 2\nCorner.N Corner.E");
	}

	@Test(expected = ParseException.class)
	public void tooFewColumns() throws ParseException {
		parse("2 2\nCorner.N\nCorner.W");
	}

	@Test(expected = ParseException.class)
	public void tooManyRows() throws ParseException {
		parse("2 1\nCorner.N Corner.E\nCorner.W Corner.S");
	}

	@Test(expected = ParseException.class)
	public void tooManyColumns() throws ParseException {
		parse("1 2\nCorner.N Corner.E\nCorner.W Corner.S");
	}

	@Test(expected = ParseException.class)
	public void barcodeUnsupported() throws ParseException {
		parse("1 1\nCorner.N.01");
	}

	@Test
	public void startPosition() throws ParseException {
		String source = "2 2\nStraight.N.S2E DeadEnd.N.S3S\nCorner.W.S0N Corner.S.S1W";
		IMaze maze = parse(source);

		// Player 0 in bottom left facing north
		assertEquals(maze.getStartPose(0).getLocation(), new Point(20, 20));
		assertEquals(maze.getStartPose(0).getHeading(), 90d, 0.1d);
		// Player 1 in bottom right facing west
		assertEquals(maze.getStartPose(1).getLocation(), new Point(60, 20));
		assertEquals(maze.getStartPose(1).getHeading(), 180d, 0.1d);
		// Player 2 in top left facing east
		assertEquals(maze.getStartPose(2).getLocation(), new Point(20, 60));
		assertEquals(maze.getStartPose(2).getHeading(), 0d, 0.1d);
		// Player 3 in top right facing south
		assertEquals(maze.getStartPose(3).getLocation(), new Point(60, 60));
		assertEquals(maze.getStartPose(3).getHeading(), -90d, 0.1d);
	}

	@Test
	public void singleTile() throws ParseException {
		String source = "Straight.E.37";
		Tile tile = Parser.parseTile(0, 0, source);

		checkWalls(tile, NORTH, SOUTH);
		assertEquals(new LongPoint(0, 0), tile.getPosition());
		assertEquals(tile.getBarcode().getValue(), 37);
	}

	@Test
	public void seesaw() throws ParseException {
		String source = "4 1\nStraight.E.11 Seesaw.W Seesaw.E Straight.E.13";
		IMaze maze = parse(source);

		// (0, 0) Barcode 11
		Tile tile00 = maze.getTileAt(new LongPoint(0, 0));
		checkWalls(tile00, NORTH, SOUTH);
		assertTrue(tile00.hasBarcode());
		assertEquals(tile00.getBarcode().getValue(), 11);

		// (1, 0) Seesaw at side 11, open
		Tile tile10 = maze.getTileAt(new LongPoint(1, 0));
		checkWalls(tile10, NORTH, SOUTH);
		assertTrue(tile10.isSeesaw());
		assertTrue(tile10.isSeesawOpen());
		assertEquals(tile10.getSeesawBarcode().getValue(), 11);

		// (2, 0) Seesaw at side 13, closed
		Tile tile20 = maze.getTileAt(new LongPoint(2, 0));
		checkWalls(tile20, NORTH, SOUTH);
		assertTrue(tile20.isSeesaw());
		assertFalse(tile20.isSeesawOpen());
		assertEquals(tile20.getSeesawBarcode().getValue(), 13);

		// (3, 0) Barcode 13
		Tile tile30 = maze.getTileAt(new LongPoint(3, 0));
		checkWalls(tile30, NORTH, SOUTH);
		assertTrue(tile30.hasBarcode());
		assertEquals(tile30.getBarcode().getValue(), 13);
	}

	@Test
	public void seesawVertical() throws ParseException {
		String source = "1 4\nStraight.N.11\nSeesaw.N\nSeesaw.S\nStraight.N.13";
		IMaze maze = parse(source);

		// (0, 3) Barcode 11
		Tile tile03 = maze.getTileAt(new LongPoint(0, 3));
		checkWalls(tile03, WEST, EAST);
		assertTrue(tile03.hasBarcode());
		assertEquals(tile03.getBarcode().getValue(), 11);

		// (0, 2) Seesaw at side 11, open
		Tile tile02 = maze.getTileAt(new LongPoint(0, 2));
		checkWalls(tile02, WEST, EAST);
		assertTrue(tile02.isSeesaw());
		assertTrue(tile02.isSeesawOpen());
		assertEquals(tile02.getSeesawBarcode().getValue(), 11);

		// (0, 1) Seesaw at side 13, closed
		Tile tile01 = maze.getTileAt(new LongPoint(0, 1));
		checkWalls(tile01, WEST, EAST);
		assertTrue(tile01.isSeesaw());
		assertFalse(tile01.isSeesawOpen());
		assertEquals(tile01.getSeesawBarcode().getValue(), 13);

		// (0, 0) Barcode 13
		Tile tile00 = maze.getTileAt(new LongPoint(0, 0));
		checkWalls(tile00, WEST, EAST);
		assertTrue(tile00.hasBarcode());
		assertEquals(tile00.getBarcode().getValue(), 13);
	}

	@Test
	public void parseTileBarcode() throws ParseException {
		Tile parsedTile = Parser.parseTile(80, 120, "Straight.N.11");
		// Position
		assertEquals(parsedTile.getX(), 80);
		assertEquals(parsedTile.getY(), 120);
		// Edges
		checkWalls(parsedTile, WEST, EAST);
		// Barcode
		assertEquals(parsedTile.getBarcode().getValue(), 11);
	}

	@Test
	public void stringifyBarcode() throws ParseException {
		String expected = "Straight.E.37";
		IMaze maze = parse("1 1\n" + expected);
		String actual = Parser.stringify(maze, new LongPoint(0, 0));

		assertEquals(expected, actual);
	}

	@Test
	public void stringifySeesaw() throws ParseException {
		String source = "4 1\nStraight.E.11 Seesaw.W Seesaw.E Straight.E.13";
		IMaze maze = parse(source);

		assertEquals("Straight.E.11", Parser.stringify(maze, new LongPoint(0, 0)));
		assertEquals("Seesaw.W", Parser.stringify(maze, new LongPoint(1, 0)));
		assertEquals("Seesaw.E", Parser.stringify(maze, new LongPoint(2, 0)));
		assertEquals("Straight.E.13", Parser.stringify(maze, new LongPoint(3, 0)));
	}

	/**
	 * Parse the given source into a new maze.
	 */
	private IMaze parse(CharSequence source) throws ParseException {
		IMaze maze = new Maze();
		Parser parser = new Parser(maze);
		parser.parse(source);
		return maze;
	}

	/**
	 * Check whether the tiles has walls only at the given expected
	 * orientations.
	 */
	private void checkWalls(Tile tile, Orientation... expectedEdges) {
		EnumSet<Orientation> expectedEdgesSet = Sets.newEnumSet(Arrays.asList(expectedEdges), Orientation.class);
		for (Orientation orientation : Orientation.values()) {
			boolean expectWall = expectedEdgesSet.contains(orientation);
			String message = (expectWall ? "Expected" : "Unexpected") + " " + orientation + " wall at "
					+ tile.getPosition();
			boolean isWall = tile.getEdgeAt(orientation).getType() == EdgeType.WALL;
			assertEquals(message, expectWall, isWall);
		}
	}

}
