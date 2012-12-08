package mazestormer.barcode;

public enum BarcodeSpeed {
	LOW(2), HIGH(9), UPPERBOUND(20), LOWERBOUND(0);
	
	private BarcodeSpeed(double barcodeSpeedValue) {
		setBarcodeSpeedValue(barcodeSpeedValue);
	}

	private double barcodeSpeedValue;

	public double getBarcodeSpeedValue() {
		return this.barcodeSpeedValue;
	}

	public void setBarcodeSpeedValue(double value) {
		this.barcodeSpeedValue = Math.min(Math.max(LOWERBOUND.getBarcodeSpeedValue(), value),UPPERBOUND.getBarcodeSpeedValue());
	}
}
