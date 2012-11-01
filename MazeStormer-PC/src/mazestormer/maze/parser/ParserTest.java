package mazestormer.maze.parser;

import static mazestormer.maze.Orientation.*;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.EnumSet;

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
		Maze maze = parse(source);

		Tile tile = maze.getTileAt(new LongPoint(0, 0));
		checkEdges(tile, expectedEdges);
	}

	@Test
	public void single() throws ParseException {
		final Orientation[] expectedEdges = new Orientation[] { WEST, EAST };

		String source = "1 1\nStraight.N.00";
		Maze maze = parse(source);

		Tile tile = maze.getTileAt(new LongPoint(0, 0));
		checkEdges(tile, expectedEdges);
	}

	@Test
	public void square() throws ParseException {
		String source = "2 2\nCorner.N Corner.E\nCorner.W Corner.S";
		Maze maze = parse(source);

		// Bottom left corner
		checkEdges(maze.getTileAt(new LongPoint(0, 0)), SOUTH, WEST);
		// Bottom right corner
		checkEdges(maze.getTileAt(new LongPoint(1, 0)), SOUTH, EAST);
		// Top left corner
		checkEdges(maze.getTileAt(new LongPoint(0, 1)), NORTH, WEST);
		// Top right corner
		checkEdges(maze.getTileAt(new LongPoint(1, 1)), NORTH, EAST);
	}

	@Test
	public void horizontal() throws ParseException {
		String source = "3 1\nDeadEnd.W Straight.E.00 DeadEnd.E";
		Maze maze = parse(source);

		// Left
		checkEdges(maze.getTileAt(new LongPoint(0, 0)), NORTH, SOUTH, WEST);
		// Center
		checkEdges(maze.getTileAt(new LongPoint(1, 0)), NORTH, SOUTH);
		// Right
		checkEdges(maze.getTileAt(new LongPoint(2, 0)), NORTH, SOUTH, EAST);
	}

	@Test
	public void vertical() throws ParseException {
		String source = "1 3\nDeadEnd.N\nStraight.N.00\nDeadEnd.S";
		Maze maze = parse(source);

		// Top
		checkEdges(maze.getTileAt(new LongPoint(0, 2)), WEST, EAST, NORTH);
		// Middle
		checkEdges(maze.getTileAt(new LongPoint(0, 1)), WEST, EAST);
		// Bottom
		checkEdges(maze.getTileAt(new LongPoint(0, 0)), WEST, EAST, SOUTH);
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

	/**
	 * Parse the given source into a new maze.
	 */
	private Maze parse(CharSequence source) throws ParseException {
		Maze maze = new Maze();
		Parser parser = new Parser(maze);
		parser.parse(source);
		return maze;
	}

	/**
	 * Check whether the tiles has edges only at the given expected orientations.
	 */
	private void checkEdges(Tile tile, Orientation... expectedEdges) {
		EnumSet<Orientation> expectedEdgesSet = Sets.newEnumSet(Arrays.asList(expectedEdges), Orientation.class);
		for (Orientation orientation : Orientation.values()) {
			boolean expectEdge = expectedEdgesSet.contains(orientation);
			String message = (expectEdge ? "Expected" : "Unexpected") + " " + orientation + " edge at "
					+ tile.getPosition();
			assertEquals(message, expectEdge, tile.hasEdgeAt(orientation));
		}
	}
}
