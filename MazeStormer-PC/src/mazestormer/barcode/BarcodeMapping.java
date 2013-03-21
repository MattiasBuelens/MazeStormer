package mazestormer.barcode;

public abstract class BarcodeMapping {

	protected abstract IAction getActionAt(int barcode);

	public IAction getAction(byte barcode) {
		// Find action for forward barcode
		IAction action = getActionAt((int) barcode);
		if (action != null)
			return action;
		// Find action for reversed barcode
		byte reverseBarcode = Barcode.reverse(barcode);
		IAction reverseAction = getActionAt((int) reverseBarcode);
		if (reverseAction != null)
			return reverseAction;
		// No action found
		return new NoAction();
	}

	public IAction getAction(Barcode barcode) {
		return getAction(barcode.getValue());
	}

}
