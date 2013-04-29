package mazestormer.barcode;

public interface BarcodeScannerListener {

	/**
	 * Triggered when the start of a barcode was found.
	 */
	public void onStartBarcode();

	/**
	 * Triggered when the barcode was successfully read.
	 */
	public void onEndBarcode(Barcode barcode);

}
