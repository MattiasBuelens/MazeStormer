package mazestormer.barcode;

import mazestormer.game.GameRunner;

public class TeamTreasureTrekBarcodeMapping extends BarcodeMapping {

	private static IAction[] actions = new IAction[40];

	public TeamTreasureTrekBarcodeMapping(GameRunner gameRunner) {
		createActions(gameRunner);
	}

	private void createActions(GameRunner gameRunner) {
		actions[0] = new ObjectFoundAction(0, 0, gameRunner);
		actions[1] = new ObjectFoundAction(1, 0, gameRunner);
		actions[2] = new ObjectFoundAction(2, 0, gameRunner);
		actions[3] = new ObjectFoundAction(3, 0, gameRunner);
		actions[4] = new ObjectFoundAction(0, 1, gameRunner);
		actions[5] = new ObjectFoundAction(1, 1, gameRunner);
		actions[6] = new ObjectFoundAction(2, 1, gameRunner);
		actions[7] = new ObjectFoundAction(3, 1, gameRunner);
		actions[11] = new SeesawAction();
		actions[13] = new SeesawAction();
		actions[15] = new SeesawAction();
		actions[17] = new SeesawAction();
		actions[19] = new SeesawAction();
		actions[21] = new SeesawAction();
	}

	@Override
	public IAction getActionAt(int barcode) {
		if (barcode >= 0 && barcode < actions.length) {
			return actions[barcode];
		} else {
			return null;
		}
	}

}
