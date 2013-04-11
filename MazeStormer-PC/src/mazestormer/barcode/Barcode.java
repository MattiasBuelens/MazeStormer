package mazestormer.barcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Barcode {

	private final byte value;
	private final List<Integer> barWidths;

	private static final int nbBars = 6;

	public Barcode(byte value) {
		this.value = value;
		this.barWidths = Collections.unmodifiableList(getWidths(value));
	}

	public byte getValue() {
		return value;
	}

	public Barcode reverse() {
		return new Barcode(reverse(getValue()));
	}

	/**
	 * Get the widths of the bars in this barcode, with leading and terminating
	 * black bars.
	 */
	public List<Integer> getWidths() {
		return barWidths;
	}

	/**
	 * Get the number of bars in a barcode, without leading and terminating
	 * black bars.
	 */
	public static int getNbValueBars() {
		return nbBars;
	}

	/**
	 * Get the number of bars in a barcode, with leading and terminating black
	 * bars.
	 */
	public static int getNbBars() {
		return getNbValueBars() + 2;
	}

	/**
	 * Reverse the given barcode.
	 * 
	 * @param barcode
	 *            The barcode value to reverse.
	 * 
	 * @return The barcode value in reversed bit order.
	 */
	public static byte reverse(byte barcode) {
		return (byte) (Integer.reverse(barcode) >>> (Integer.SIZE - getNbValueBars()));
	}

	private static List<Integer> getWidths(byte value) {
		List<Integer> widths = new ArrayList<Integer>();

		// Add leading black bar
		int index = 0;
		boolean lastBit = false;
		widths.add(1);

		// Loop over bits in barcode
		for (int i = nbBars - 1; i >= 0; i--) {
			boolean currentBit = ((value >>> i) & 1) == 1;
			if (lastBit == currentBit) {
				// Increment width of current bar
				widths.set(index, widths.get(index) + 1);
			} else {
				// Bit changed, start new bar
				widths.add(1);
				lastBit = currentBit;
				index++;
			}
		}

		// Add trailing black bar
		if (lastBit) {
			// Add terminating black bar
			widths.add(1);
		} else {
			// Increment last black bar
			widths.set(index, widths.get(index) + 1);
		}

		return widths;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other.getClass() != Barcode.class)
			return false;

		Barcode otherBarcode = (Barcode) other;
		// Test forward
		if (otherBarcode.getValue() == getValue())
			return true;
		// Test reverse
		return otherBarcode.getValue() == reverse(getValue());
	}

	@Override
	public int hashCode() {
		// Use a commutative operation on own value and reversed value
		// Exclusive OR keeps things nicely scattered
		return getValue() ^ reverse(getValue());
	}

}
