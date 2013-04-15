package mazestormer.barcode;

import java.util.HashMap;
import java.util.Map;

public class ExplorerBarcodeMapping implements BarcodeMapping {

	private Map<Barcode, IAction> actions = new HashMap<>();
	
	public ExplorerBarcodeMapping() {
		createActions();
	}
	
	@Override
	public IAction getAction(Barcode barcode) {
		if(actions.containsKey(barcode)) return actions.get(barcode);
		else return null;
	}

	private void createActions() {
		actions.put( new Barcode(5), new RotateCounterClockwiseAction());
		actions.put( new Barcode(9), new RotateClockwiseAction());
		actions.put( new Barcode(13), new CheckPointAction());
		actions.put( new Barcode(15), new SoundAction());
		actions.put( new Barcode(19), new WaitAction());
		actions.put( new Barcode(25), new LowSpeedAction());
		actions.put( new Barcode(37), new HighSpeedAction());
		actions.put( new Barcode(55), new GoalAction());
	}

}
