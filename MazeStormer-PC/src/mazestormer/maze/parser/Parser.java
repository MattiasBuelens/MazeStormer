package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import mazestormer.barcode.Barcode;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.util.LongPoint;

public class Parser {

	private final IMaze maze;
	private final List<PositionedToken> seesawTokens = new ArrayList<PositionedToken>();

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
		IMaze maze = getMaze();

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

	public static String stringify(IMaze maze, LongPoint position) {
		StringBuilder token = new StringBuilder();
		Tile tile = maze.getTileAt(position);

		if (tile.isSeesaw()) {
			// Get orientation towards barcode tile
			Tile barcodeTile = maze.getBarcodeTile(tile.getSeesawBarcode());
			Orientation orientation = tile.orientationTo(barcodeTile);
			// Write seesaw token
			token.append(TileType.SEESAW.getName());
			token.append('.').append(orientation.getShortName());
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

}
