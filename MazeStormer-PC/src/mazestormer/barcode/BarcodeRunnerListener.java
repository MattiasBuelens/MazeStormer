package mazestormer.barcode;

public interface BarcodeRunnerListener {

	/**
	 * Triggered when the start of a barcode was found.
	 */
	public void onStartBarcode();

	/**
	 * Triggered when the barcode was successfully read.
	 * 
	 * <p>
	 * The default implementation logs the read barcode and performs the
	 * associated action.
	 * </p>
	 */
	public void onEndBarcode(byte barcode);

}
