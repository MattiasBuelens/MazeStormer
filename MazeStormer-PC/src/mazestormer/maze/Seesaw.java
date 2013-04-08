package mazestormer.maze;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.TeamTreasureTrekBarcodeMapping;

public class Seesaw {

	private final Barcode lowestBarcode;
	private final Barcode highestBarcode;

	private boolean isHighOpen = false;

	public Seesaw(Barcode barcode, Barcode otherBarcode) {
		if (barcode.getValue() < otherBarcode.getValue()) {
			this.lowestBarcode = barcode;
			this.highestBarcode = otherBarcode;
		} else {
			this.lowestBarcode = otherBarcode;
			this.highestBarcode = barcode;
		}
	}

	public Seesaw(Barcode barcode) {
		this(barcode, TeamTreasureTrekBarcodeMapping.getOtherSeesawBarcode(barcode));
	}

	public Seesaw(byte barcodeValue) {
		this(new Barcode(barcodeValue));
	}

	public Barcode getLowestBarcode() {
		return lowestBarcode;
	}

	public Barcode getHighestBarcode() {
		return highestBarcode;
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

	public void flip() {
		this.isHighOpen = !this.isHighOpen;
	}

	public boolean isOpen(Barcode barcode) {
		if (barcode.getValue() == lowestBarcode.getValue()) {
			return isLowOpen();
		} else if (barcode.getValue() == highestBarcode.getValue()) {
			return isHighOpen();
		} else {
			throw new IllegalArgumentException("Barcode does not belong to this seesaw.");
		}
	}

	public void setOpen(Barcode barcode) {
		if (barcode.getValue() == lowestBarcode.getValue()) {
			setLowOpen();
		} else if (barcode.getValue() == highestBarcode.getValue()) {
			setHighOpen();
		} else {
			throw new IllegalArgumentException("Barcode does not belong to this seesaw.");
		}
	}
}
