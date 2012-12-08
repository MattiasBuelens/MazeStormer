package mazestormer.barcode;

public enum BarcodeSpeed {
	LOW(2d), HIGH(9d), UPPERBOUND(20d), LOWERBOUND(0d);
	
	private BarcodeSpeed(double barcodeSpeedValue) {
		this.barcodeSpeedValue = barcodeSpeedValue;
	}

	private double barcodeSpeedValue;

	public double getBarcodeSpeedValue() {
		return this.barcodeSpeedValue;
	}

	public void setBarcodeSpeedValue(double value) {
		this.barcodeSpeedValue = Math.min(Math.max(LOWERBOUND.getBarcodeSpeedValue(), value),UPPERBOUND.getBarcodeSpeedValue());
	}
}
