package mazestormer.maze.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;

import mazestormer.maze.Orientation;
import mazestormer.maze.TileType;

public class TileToken implements Token {

	private final TileType type;
	private final Orientation orientation;
	private final byte barcode;

	private TileToken(TileType type, Orientation orientation, byte barcode) {
		this.type = checkNotNull(type);
		this.orientation = orientation;
		this.barcode = barcode;
	}

	public TileType getType() {
		return type;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public byte getBarcode() {
		return barcode;
	}

	/**
	 * Parse a tile token.
	 * 
	 * @param typeName
	 * 			The name of the tile type.
	 * @param orientationName
	 * 			The short name of the tile's orientation.
	 * @param barcodeString
	 * 			The string representation of the barcode.
	 * 
	 * @return	The parsed tile token.
	 * @throws	ParseException
	 */
	public static TileToken parse(String typeName, String orientationName, String barcodeString) throws ParseException {
		TileType type = TileType.byName(typeName);
		Orientation orientation = null;
		byte barcode = 0;

		if (type == null) {
			throw new ParseException("Invalid tile type:" + typeName, 0);
		}

		if (type.hasOrientation()) {
			orientation = Orientation.byShortName(orientationName);
			if (orientation == null) {
				throw new ParseException("Invalid orientation: " + orientationName, 0);
			}
		}

		if (type.supportsBarcode()) {
			try {
				barcode = Byte.parseByte(barcodeString, 10);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid barcode: " + barcodeString, 0);
			}
		}

		return new TileToken(type, orientation, barcode);
	}

//	/**
//	 * Construct a tile token from a given tile.
//	 * 
//	 * @param tile
//	 * 			The tile.
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
