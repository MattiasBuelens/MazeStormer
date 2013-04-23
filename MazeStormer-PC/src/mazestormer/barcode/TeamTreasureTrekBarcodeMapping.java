package mazestormer.barcode;

import java.util.HashMap;
import java.util.Map;

import mazestormer.command.game.GameRunner;

public class TeamTreasureTrekBarcodeMapping extends BarcodeMapping {

	private static IAction[] actions = new IAction[40];
	
	private static Map<Barcode, Barcode> barcodeMapping = new HashMap<>();
	
	static {
		barcodeMapping.put(new Barcode((byte) 11), new Barcode((byte) 13));
		barcodeMapping.put(new Barcode((byte) 13), new Barcode((byte) 11));
		barcodeMapping.put(new Barcode((byte) 15), new Barcode((byte) 17));
		barcodeMapping.put(new Barcode((byte) 17), new Barcode((byte) 15));
		barcodeMapping.put(new Barcode((byte) 19), new Barcode((byte) 21));
		barcodeMapping.put(new Barcode((byte) 21), new Barcode((byte) 19));
	}

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
		actions[11] = new SeesawAction(gameRunner, 11);
		actions[13] = new SeesawAction(gameRunner, 13);
		actions[15] = new SeesawAction(gameRunner, 15);
		actions[17] = new SeesawAction(gameRunner, 17);
		actions[19] = new SeesawAction(gameRunner, 19);
		actions[21] = new SeesawAction(gameRunner, 21);
	}

	@Override
	public IAction getActionAt(int barcode) {
		if (barcode >= 0 && barcode < actions.length) {
			return actions[barcode];
		} else {
			return null;
		}
	}
	
	public static Barcode getOtherSeesawBarcode(Barcode barcode) {
		return barcodeMapping.get(barcode);
	}

}
