package mazestormer.barcode;

import mazestormer.player.Game;

public class TeamTreasureTrekBarcodeMapping extends BarcodeMapping {

	private static IAction[] actions = new IAction[40];

	public TeamTreasureTrekBarcodeMapping(Game game) {
		createActions(game);
	}

	private void createActions(Game game) {
		actions[0] = new ObjectFoundAction(0, 0);
		actions[1] = new ObjectFoundAction(1, 0);
		actions[2] = new ObjectFoundAction(2, 0);
		actions[3] = new ObjectFoundAction(3, 0);
		actions[4] = new ObjectFoundAction(0, 1);
		actions[5] = new ObjectFoundAction(1, 1);
		actions[6] = new ObjectFoundAction(2, 1);
		actions[7] = new ObjectFoundAction(3, 1);
		actions[11] = new SeesawAction();
		actions[13] = new SeesawAction();
		actions[15] = new SeesawAction();
		actions[17] = new SeesawAction();
		actions[19] = new SeesawAction();
		actions[21] = new SeesawAction();
		actions[55] = new NoAction();
		actions[47] = new NoAction();
		actions[43] = new NoAction();
		actions[39] = new NoAction();
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
