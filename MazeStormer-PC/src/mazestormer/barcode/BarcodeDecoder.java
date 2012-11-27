package mazestormer.barcode;

public class BarcodeDecoder {

	private static final int RANGE = 64;
	private static IAction[] actions = new IAction[RANGE];

	static {
		setActions();
	}

	private static void setActions() {
		actions[5] = new RotateCounterClockwiseAction();
		actions[9] = new RotateClockwiseAction();
		actions[15] = new SoundAction();
		actions[19] = new WaitAction();
		actions[25] = new LowSpeedAction();
		actions[37] = new HighSpeedAction();
		actions[55] = new GoalAction();
	}

	public static IAction[] getActions() {
		return actions.clone();
	}

	private static IAction getActionAt(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= RANGE)
			return new NoAction();
		if (actions[index] == null)
			return new NoAction();
		return actions[index];
	}

	public static IAction getAction(byte barcode) {
		return getActionAt((int) barcode);
	}
}
