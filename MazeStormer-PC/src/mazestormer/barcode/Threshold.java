package mazestormer.barcode;

public enum Threshold {
	/**
	 * Black to white or brown. For barcode scanner.
	 */
	BLACK_WHITE(400),

	/**
	 * White to black. For barcode scanner.
	 */
	WHITE_BLACK(400),

	/**
	 * White to brown. For line finder.
	 */
	WHITE_BROWN(530);

	private Threshold(int thresholdValue) {
		setThresholdValue(thresholdValue);
	}

	private int thresholdValue;

	public int getThresholdValue() {
		return this.thresholdValue;
	}

	public void setThresholdValue(int value) {
		this.thresholdValue = Math.max(0, value);
	}
}
