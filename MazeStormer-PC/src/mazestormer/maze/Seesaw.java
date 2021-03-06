package mazestormer.maze;

import java.util.HashMap;
import java.util.Map;

import mazestormer.barcode.Barcode;

public class Seesaw {

	private final Barcode lowestBarcode;
	private final Barcode highestBarcode;

	private static final Map<Barcode, Barcode> otherBarcode = new HashMap<Barcode, Barcode>() {
		private static final long serialVersionUID = 1L;
		{
			put(new Barcode(11), new Barcode(13));
			put(new Barcode(13), new Barcode(11));
			put(new Barcode(15), new Barcode(17));
			put(new Barcode(17), new Barcode(15));
			put(new Barcode(19), new Barcode(21));
			put(new Barcode(21), new Barcode(19));
		}
	};

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
		this(barcode, getOtherBarcode(barcode));
	}

	public static boolean isSeesawBarcode(Barcode barcode) {
		return otherBarcode.containsKey(barcode);
	}

	public static Barcode getOtherBarcode(Barcode barcode) {
		return otherBarcode.get(barcode);
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

	public Barcode getOpenBarcode() {
		return isHighOpen() ? getHighestBarcode() : getLowestBarcode();
	}

	public Barcode getClosedBarcode() {
		return isHighOpen() ? getLowestBarcode() : getHighestBarcode();
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
		if (barcode.equals(lowestBarcode)) {
			return isLowOpen();
		} else if (barcode.equals(highestBarcode)) {
			return isHighOpen();
		} else {
			throw new IllegalArgumentException(
					"Barcode does not belong to this seesaw.");
		}
	}

	public void setOpen(Barcode barcode) {
		if (barcode.equals(lowestBarcode)) {
			setLowOpen();
		} else if (barcode.equals(highestBarcode)) {
			setHighOpen();
		} else {
			throw new IllegalArgumentException(
					"Barcode does not belong to this seesaw.");
		}
	}

	public void setClosed(Barcode barcode) {
		setOpen(barcode.equals(highestBarcode) ? lowestBarcode : highestBarcode);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (!(other instanceof Seesaw))
			return false;
		Seesaw otherSeesaw = (Seesaw) other;
		return this.highestBarcode.equals(otherSeesaw.getHighestBarcode())
				&& this.lowestBarcode.equals(otherSeesaw.getLowestBarcode());
	}

	@Override
	public int hashCode() {
		return this.highestBarcode.hashCode();
	}
}
