package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;

public class BarcodeDecoder {

	static final int RANGE = (1 << Barcode.getNbValueBars());

	private static IAction getActionAt(int index, ModeBarcodeMapping mbm) {
		checkNotNull(mbm);
		if (index < 0 || index >= RANGE)
			return new NoAction();
		return mbm.getActions()[index];
	}

	public static IAction getAction(byte barcode, ModeBarcodeMapping mbm) {
		checkNotNull(mbm);
		// Find action for forward barcode
		IAction action = getActionAt((int) barcode, mbm);
		if (action != null)
			return action;
		// Find action for reversed barcode
		byte reverseBarcode = Barcode.reverse(barcode);
		IAction reverseAction = getActionAt((int) reverseBarcode, mbm);
		if (reverseAction != null)
			return reverseAction;
		// No action found
		return new NoAction();
	}

	public static IAction getAction(Barcode barcode, ModeBarcodeMapping mbm) {
		checkNotNull(mbm);
		return getAction(barcode.getValue(), mbm);
	}
}