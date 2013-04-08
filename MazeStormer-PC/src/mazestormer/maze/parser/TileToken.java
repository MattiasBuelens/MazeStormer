package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.regex.Matcher;

import mazestormer.maze.Orientation;
import mazestormer.maze.TileType;

public final class TileToken implements Token {

	private final TileType type;
	private final Orientation orientation;
	private final Option option;

	private TileToken(TileType type, Orientation orientation, Option option) {
		this.type = checkNotNull(type);
		this.orientation = orientation;
		this.option = option;
	}

	public TileType getType() {
		return type;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Option getOption() {
		return option;
	}

	/**
	 * Parse a tile token.
	 * 
	 * @param typeName
	 *            The name of the tile type.
	 * @param orientationName
	 *            The short name of the tile's orientation.
	 * @param optionString
	 *            The additional option: a barcode string, an object or a start
	 *            position.
	 * 
	 * @return The parsed tile token.
	 * @throws ParseException
	 */
	public static TileToken parse(String typeName, String orientationName, String optionString) throws ParseException {
		TileType type = TileType.byName(typeName);
		Orientation orientation = null;
		Option option = null;

		if (type == null) {
			throw new ParseException("Invalid tile type:" + typeName, 0);
		}

		if (type.hasOrientation()) {
			orientation = Orientation.byShortName(orientationName);
			if (orientation == null) {
				throw new ParseException("Invalid orientation: " + orientationName, 0);
			}
		}

		if (optionString != null) {
			for (OptionMatcher optionMatcher : OptionMatcher.values()) {
				Matcher matcher = optionMatcher.matcher(optionString);
				if (matcher.matches()) {
					option = optionMatcher.parse(matcher.toMatchResult());
					break;
				}
			}
			if (option == null) {
				throw new ParseException("Invalid option: " + optionString, 0);
			}
		}

		return new TileToken(type, orientation, option);
	}

//	/**
//	 * Construct a tile token from a given tile.
//	 * 
//	 * @param tile
//	 *            The tile.
//	 * 
//	 * @return The tile token, or null if an invalid tile was given.
//	 */
//	public static TileToken fromTile(Tile tile) {
//		if (tile == null)
//			return null;
//
//		// Build EnumSet of edge orientations for faster comparing
//		EnumSet<Orientation> tileEdges = EnumSet.noneOf(Orientation.class);
//		for (Edge edge : tile.getEdges()) {
//			tileEdges.add(edge.getOrientationFrom(tile.getPosition()));
//		}
//		int nbEdges = tileEdges.size();
//
//		// Loop over all tile types and orientations
//		outer: for (TileType type : TileType.values()) {
//			for (Orientation orientation : Orientation.values()) {
//				// Get edges from tile type in this orientation
//				EnumSet<Orientation> typeEdges = type.getEdges(orientation);
//				// If amount of edges differ, skip this tile type
//				if (typeEdges.size() != nbEdges) {
//					continue outer;
//				}
//				// Compare edges
//				if (typeEdges.containsAll(tileEdges)) {
//					// TODO Pass in tile.getBarcode() as third argument
//					return new TileToken(type, orientation, (byte) 0);
//				}
//			}
//		}
//		return null;
//	}

}
