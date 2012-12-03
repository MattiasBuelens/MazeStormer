package mazestormer.maze;

public class Barcode {
	
	private final byte value;
	private static final int BARCODE_LENGTH = 6;
	
	public Barcode(byte value) {
		this.value = value;
	}
	
	public Barcode reverse() {
		return new Barcode(reverse(getValue()));
	}
	
	public static byte reverse(byte input) {
		return (byte) (Integer.reverse(input) >>> (Integer.SIZE - BARCODE_LENGTH));
	}

	public byte getValue() {
		return value;
	}
}
