package mazestormer.maze.parser;

import java.text.ParseException;

import mazestormer.barcode.Barcode;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.util.LongPoint;

public class BarcodeOption implements Option {

	private final byte barcode;

	public BarcodeOption(byte barcode) {
		this.barcode = barcode;
	}

	public byte getBarcode() {
		return barcode;
	}

	@Override
	public void apply(Maze maze, LongPoint tilePosition, TileToken token) throws ParseException {
		if (token.getType().supportsBarcode()) {
			maze.setBarcode(tilePosition, getBarcode());
		} else {
			throw new ParseException("Tile does not support barcodes: " + tilePosition, 0);
		}
	}

	@Override
	public void apply(Tile tile, TileToken token) throws ParseException {
		if (token.getType().supportsBarcode()) {
			tile.setBarcode(new Barcode(getBarcode()));
		} else {
			throw new ParseException("Tile does not support barcodes", 0);
		}
	}

}
