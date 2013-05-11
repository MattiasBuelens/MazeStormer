package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;

import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.util.LongPoint;

public class Parser {

	private final IMaze maze;

	/**
	 * Create a new parser which outputs to the given maze.
	 * 
	 * @param maze
	 *            The output maze.
	 */
	public Parser(IMaze maze) {
		this.maze = checkNotNull(maze);
	}

	/**
	 * Get the output maze.
	 */
	public IMaze getMaze() {
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
		IMaze maze = getMaze();
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
				for (Orientation orientation : token.getType().getWalls(token.getOrientation())) {
					maze.setEdge(position, orientation, EdgeType.WALL);
				}
				for (Orientation orientation : token.getType().getOpenings(token.getOrientation())) {
					maze.setEdge(position, orientation, EdgeType.OPEN);
				}
				// Set option
				if (token.getOption() != null) {
					token.getOption().apply(maze, position, token);
				}
				// Store seesaw tokens for later handling
				if (token.getType() == TileType.SEESAW) {
					maze.setSeesaw(position, token.getOrientation());
				}
			}
		}

		// Ensure end of file
		tokenizer.getEOFToken();
	}

	/**
	 * Parse a single tile token.
	 * 
	 * @param x
	 *            The X-position of the tile.
	 * @param y
	 *            The Y-position of the tile.
	 * @param tileToken
	 *            The tile token to parse.
	 * 
	 * @throws ParseException
	 *             If the token was invalid.
	 */
	public static Tile parseTile(long x, long y, String tileToken) throws ParseException {
		Tokenizer tokenizer = new Tokenizer(tileToken);
		// Create tile
		LongPoint position = new LongPoint(x, y);
		Tile tile = new Tile(position);
		// Parse
		TileToken token = tokenizer.getTileToken(false);
		// Set edges
		for (Orientation orientation : token.getType().getWalls(token.getOrientation())) {
			tile.setEdge(orientation, EdgeType.WALL);
		}
		for (Orientation orientation : token.getType().getOpenings(token.getOrientation())) {
			tile.setEdge(orientation, EdgeType.OPEN);
		}
		// Set option
		if (token.getOption() != null) {
			token.getOption().apply(tile, token);
		}
		// Seesaw
		if (token.getType() == TileType.SEESAW) {
			tile.setSeesawOrientation(token.getOrientation());
		}
		return tile;
	}

	/**
	 * Stringify a single tile in a maze.
	 * 
	 * @param maze
	 *            The maze.
	 * @param position
	 *            The tile position to stringify.
	 */
	public static String stringify(IMaze maze, LongPoint position) {
		StringBuilder token = new StringBuilder();
		Tile tile = maze.getTileAt(position);

		if (tile.getSeesawOrientation() != null) {
			// Write seesaw token
			token.append(TileType.SEESAW.getName());
			token.append('.').append(tile.getSeesawOrientation());
		} else {
			// Write shape
			TileShape shape = tile.getShape();
			token.append(shape.getType().getName());
			if (shape.getType().hasOrientation()) {
				token.append('.').append(shape.getOrientation().getShortName());
			}
		}
		if (tile.hasBarcode()) {
			// Write barcode
			int barcode = tile.getBarcode().getValue();
			String barcodeString = String.format("%02d", barcode);
			token.append('.').append(barcodeString);
		}
		return token.toString();
	}

	/**
	 * Stringify a complete maze.
	 * 
	 * <p>
	 * Implementation note: The bounds are contracted by one to remove the extra
	 * tiles created by the edges around the actual maze.
	 * </p>
	 * 
	 * @param maze
	 *            The maze to stringify.
	 */
	public static String stringify(IMaze maze) {
		StringBuilder sb = new StringBuilder();
		// Dimensions
		long width = Math.max(0, maze.getMaxX() - maze.getMinX() - 1);
		long height = Math.max(0, maze.getMaxY() - maze.getMinY() - 1);
		sb.append(width).append(' ');
		sb.append(height).append('\n');
		// Tiles
		for (long y = maze.getMaxY() - 1; y > maze.getMinY(); --y) {
			for (long x = maze.getMinX() + 1; x < maze.getMaxX(); ++x) {
				sb.append(stringify(maze, new LongPoint(x, y))).append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

}
