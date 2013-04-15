package mazestormer.barcode;

import java.util.HashMap;
import java.util.Map;

import mazestormer.game.GameRunner;

public class TeamTreasureTrekBarcodeMapping implements BarcodeMapping {
	
	private static Map<Barcode, Barcode> seesawBarcodeMapping = new HashMap<>();
	private Map<Barcode, IAction> actions = new HashMap<>();
	
	static {
		seesawBarcodeMapping.put(new Barcode(11), new Barcode(13));
		seesawBarcodeMapping.put(new Barcode(13), new Barcode(11));
		seesawBarcodeMapping.put(new Barcode(15), new Barcode(17));
		seesawBarcodeMapping.put(new Barcode(17), new Barcode(15));
		seesawBarcodeMapping.put(new Barcode(19), new Barcode(21));
		seesawBarcodeMapping.put(new Barcode(21), new Barcode(19));
	}

	public TeamTreasureTrekBarcodeMapping(GameRunner gameRunner) {
		createActions(gameRunner);
	}

	private void createActions(GameRunner gameRunner) {
		actions.put(new Barcode(0), new ObjectFoundAction(0, 0, gameRunner));
		actions.put(new Barcode(1), new ObjectFoundAction(1, 0, gameRunner));
		actions.put(new Barcode(2), new ObjectFoundAction(2, 0, gameRunner));
		actions.put(new Barcode(3), new ObjectFoundAction(3, 0, gameRunner));
		actions.put(new Barcode(4), new ObjectFoundAction(0, 1, gameRunner));
		actions.put(new Barcode(5), new ObjectFoundAction(1, 1, gameRunner));
		actions.put(new Barcode(6), new ObjectFoundAction(2, 1, gameRunner));
		actions.put(new Barcode(7), new ObjectFoundAction(3, 1, gameRunner));
		actions.put(new Barcode(11), new SeesawAction(gameRunner, 11));
		actions.put(new Barcode(13), new SeesawAction(gameRunner, 13));
		actions.put(new Barcode(15), new SeesawAction(gameRunner, 15));
		actions.put(new Barcode(17), new SeesawAction(gameRunner, 17));
		actions.put(new Barcode(19), new SeesawAction(gameRunner, 19));
		actions.put(new Barcode(21), new SeesawAction(gameRunner, 21));
	}

	public IAction getAction(Barcode barcode) {
		if(actions.containsKey(barcode)) return actions.get(barcode);
		else return null;
	}
	
	public static Barcode getOtherSeesawBarcode(Barcode barcode) {
		return seesawBarcodeMapping.get(barcode);
	}

}
