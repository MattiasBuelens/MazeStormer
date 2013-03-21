package mazestormer.barcode;

public class ExplorerBarcodeMapping extends BarcodeMapping {

	@Override
	public IAction getActionAt(int barcode) {
		if (barcode >= 0 && barcode < actions.length) {
			return actions[barcode];
		} else {
			return null;
		}
	}

	private static IAction[] actions = new IAction[56];
	static {
		actions[5] = new RotateCounterClockwiseAction();
		actions[9] = new RotateClockwiseAction();
		actions[13] = new CheckPointAction();
		actions[15] = new SoundAction();
		actions[19] = new WaitAction();
		actions[25] = new LowSpeedAction();
		actions[37] = new HighSpeedAction();
		actions[55] = new GoalAction();
	}

}
