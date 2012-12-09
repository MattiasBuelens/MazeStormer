package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;

import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.util.LongPoint;

public class Parser {

	private final Maze maze;

	/**
	 * Create a new parser which outputs to the given maze.
	 * 
	 * @param maze
	 *            The output maze.
	 */
	public Parser(Maze maze) {
		this.maze = checkNotNull(maze);
	}

	/**
	 * Get the output maze.
	 */
	public Maze getMaze() {
		return maze;
	}

	/**
	 * Parse the given source into the output maze.
	 * 
	 * @param source
	 *            The source text.
	 * 
	 * @throws ParseException
	 *             If the source was invalid.
	 */
	public void parse(CharSequence source) throws ParseException {
		Maze maze = getMaze();
		Tokenizer tokenizer = new Tokenizer(source);

		// Read width and height
		long width = tokenizer.getDimensionToken(true).getValue();
		long height = tokenizer.getDimensionToken(false).getValue();

		// Read tiles
		for (long y = height - 1; y >= 0; --y) {
			// Ensure new line
			tokenizer.getNewLineToken(true);
			for (long x = 0; x < width; ++x) {
				// Skip comments before first token on new line
				TileToken token = tokenizer.getTileToken(x == 0);
				// Get position
				LongPoint position = new LongPoint(x, y);
				// Set edges
				for (Orientation orientation : token.getType().getWalls(
						token.getOrientation())) {
					maze.setEdge(position, orientation, EdgeType.WALL);
				}
				for (Orientation orientation : token.getType().getOpenings(
						token.getOrientation())) {
					maze.setEdge(position, orientation, EdgeType.OPEN);
				}
				// Set barcode
				if (token.getType().supportsBarcode() && token.getBarcode() != 0) {
					maze.setBarcode(position, token.getBarcode());
				}
			}
		}

		// Ensure end of file
		tokenizer.getEOFToken();
	}
}