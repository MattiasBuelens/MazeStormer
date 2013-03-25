package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import mazestormer.barcode.Barcode;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.TileType;
import mazestormer.util.LongPoint;

public class Parser {

	private final Maze maze;
	private final List<PositionedToken> seesawTokens = new ArrayList<PositionedToken>();

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

		// Reset state
		seesawTokens.clear();

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
					seesawTokens.add(new PositionedToken(position, token));
				}
			}
		}

		// Link seesaws
		linkSeesaws();

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
		return tile;
	}

	/**
	 * Link seesaws to barcodes.
	 */
	private void linkSeesaws() {
		Maze maze = getMaze();

		// Set seesaw barcodes
		for (PositionedToken token : seesawTokens) {
			// Find barcode tile next to seesaw
			LongPoint position = token.getPosition();
			Orientation orientation = token.getToken().getOrientation();
			LongPoint barcodePosition = orientation.shift(position);
			Barcode barcode = maze.getTileAt(barcodePosition).getBarcode();
			// Set barcode
			maze.getTileAt(position).setSeesawBarcode(barcode);
		}

		// Set seesaw states
		for (PositionedToken token : seesawTokens) {
			// Find neighboring seesaw tile
			LongPoint position = token.getPosition();
			Orientation orientation = token.getToken().getOrientation();
			LongPoint neighborPosition = orientation.shift(position, -1);
			// Get tiles
			Tile tile = maze.getTileAt(position);
			Tile neighborTile = maze.getTileAt(neighborPosition);
			// Compare barcodes and set state
			// The specification states that the seesaw is initially open
			// on the side with the lowest barcode
			boolean isLowest = tile.getSeesawBarcode().getValue() < neighborTile.getSeesawBarcode().getValue();
			tile.setSeesawOpen(isLowest);
			neighborTile.setSeesawOpen(!isLowest);
		}
	}

	/**
	 * A positioned token.
	 * 
	 * Helper class for post-processing parsed tiles.
	 */
	private class PositionedToken {

		private final LongPoint position;
		private final TileToken token;

		public PositionedToken(LongPoint position, TileToken token) {
			this.position = position;
			this.token = token;
		}

		public LongPoint getPosition() {
			return position;
		}

		public TileToken getToken() {
			return token;
		}

	}

	public static String stringify(Tile tile) {
		// TODO Auto-generated method stub
		return null;
	}

}
