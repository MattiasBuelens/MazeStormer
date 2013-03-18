package mazestormer.maze;

import mazestormer.barcode.Barcode;

public class Seesaw {

	private final Barcode lowestBarcode;
	private final Barcode highestBarcode;

	private boolean isHighOpen = false;
	private boolean isOccupied = false;

	public Seesaw(Barcode lowestBarcode, Barcode highestBarcode) {
		this.lowestBarcode = lowestBarcode;
		this.highestBarcode = highestBarcode;
	}

	public Seesaw(Barcode lowestBarcode) {
		this(lowestBarcode, new Barcode((byte) (lowestBarcode.getValue() + 2)));
	}

	public Seesaw(byte lowestBarcodeValue) {
		this(new Barcode(lowestBarcodeValue));
	}

	public boolean isHighOpen() {
		return isHighOpen;
	}

	public void setHighOpen() {
		this.isHighOpen = true;
	}

	public boolean isLowOpen() {
		return !isHighOpen();
	}

	public void setLowOpen() {
		this.isHighOpen = false;
	}

	public boolean isOpen(Barcode barcode) {
		if (barcode.getValue() == lowestBarcode.getValue()) {
			return isLowOpen();
		} else if (barcode.getValue() == highestBarcode.getValue()) {
			return isHighOpen();
		} else {
			throw new IllegalArgumentException(
					"Barcode does not belong to this seesaw.");
		}
	}

	public void setOpen(Barcode barcode) {
		if (barcode.getValue() == lowestBarcode.getValue()) {
			setLowOpen();
		} else if (barcode.getValue() == highestBarcode.getValue()) {
			setHighOpen();
		} else {
			throw new IllegalArgumentException(
					"Barcode does not belong to this seesaw.");
		}
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public boolean canEnter(Barcode barcode) {
		return !isOccupied() && isOpen(barcode);
	}

}
