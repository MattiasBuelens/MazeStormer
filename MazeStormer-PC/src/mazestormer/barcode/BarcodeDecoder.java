package mazestormer.barcode;

public class BarcodeDecoder {

	private static final int RANGE = (1 << Barcode.getNbValueBars());
	private static IAction[] actions = new IAction[RANGE];

	static {
		setActions();
	}

	private static void setActions() {
		actions[5] = new RotateCounterClockwiseAction();
		actions[9] = new RotateClockwiseAction();
		actions[13] = new CheckPointAction();
		actions[15] = new SoundAction();
		actions[19] = new WaitAction();
		actions[25] = new LowSpeedAction();
		actions[37] = new HighSpeedAction();
		actions[55] = new GoalAction();
	}

	public static IAction[] getActions() {
		return actions.clone();
	}

	private static IAction getActionAt(int index) {
		if (index < 0 || index >= RANGE)
			return new NoAction();
		return actions[index];
	}

	public static IAction getAction(byte barcode) {
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

	public static IAction getAction(Barcode barcode) {
		return getAction(barcode.getValue());
	}

}
