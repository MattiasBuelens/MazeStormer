package mazestormer.barcode.action;

import mazestormer.barcode.Barcode;

public interface BarcodeMapping {

	public IAction getAction(Barcode barcode);

}
