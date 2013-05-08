package mazestormer.barcode;

public enum Threshold {
	// TODO Thresholds need tweaking for virtual light sensor
	BLACK_WHITE(530), WHITE_BLACK(400), BLACK_BROWN(450), WHITE_BROWN(530);

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
