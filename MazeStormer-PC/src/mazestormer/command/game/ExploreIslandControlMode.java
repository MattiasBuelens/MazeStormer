package mazestormer.command.game;

import java.util.HashMap;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.command.AbstractExploreControlMode;
import mazestormer.player.Player;
import mazestormer.util.Future;

public class ExploreIslandControlMode extends AbstractExploreControlMode {

	/*
	 * Atributes
	 */

	private final BarcodeMapping exploreBarcodeMapping = new ExploreIslandBarcodeMapping();

	/*
	 * Constructor
	 */

	public ExploreIslandControlMode(Player player, GameRunner gameRunner) {
		super(player, gameRunner);
	}

	/*
	 * Getters
	 */

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	/*
	 * Driver support
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return exploreBarcodeMapping.getAction(barcode);
	}

	/*
	 * Utilities
	 */

	private class ObjectAction extends ObjectFoundAction {

		private Barcode barcode;

		private ObjectAction(Barcode barcode) {
			this.barcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			getGameRunner().setObjectTile(); // voeg info toe aan maze

			// TODO: verwijder volgende tegels uit queue? Worden ze ooit
			// toegevoegd?

			if (getObjectNumberFromBarcode(barcode) == ((GameRunner) getCommander()).getObjectNumber()) { // indien
																											// eigen
																											// barcode:
				objectFound(getTeamNumberFromBarcode(barcode));
				return super.performAction(player); // eigen voorwerp wordt
													// opgepikt
			} else {
				return null; // ?
			}
		}

		private int getObjectNumberFromBarcode(Barcode objectBarcode) {
			return (objectBarcode.getValue() % 4);
		}

		private int getTeamNumberFromBarcode(Barcode objectBarcode) {
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}

	}

	private class SeesawAction implements IAction {

		private SeesawAction(Barcode barcode) {
		}

		@Override
		public Future<?> performAction(Player player) {
			//seesaw open?
			return null;
		}

	}

	private class ExploreIslandBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeTypeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(0), new ObjectAction(new Barcode(0)));
				put(new Barcode(1), new ObjectAction(new Barcode(1)));
				put(new Barcode(2), new ObjectAction(new Barcode(2)));
				put(new Barcode(3), new ObjectAction(new Barcode(3)));
				put(new Barcode(4), new ObjectAction(new Barcode(4)));
				put(new Barcode(5), new ObjectAction(new Barcode(5)));
				put(new Barcode(6), new ObjectAction(new Barcode(6)));
				put(new Barcode(7), new ObjectAction(new Barcode(7)));
				put(new Barcode(11), new SeesawAction(new Barcode(11)));
				put(new Barcode(13), new SeesawAction(new Barcode(13)));
				put(new Barcode(15), new SeesawAction(new Barcode(15)));
				put(new Barcode(17), new SeesawAction(new Barcode(17)));
				put(new Barcode(19), new SeesawAction(new Barcode(19)));
				put(new Barcode(21), new SeesawAction(new Barcode(21)));
			}
		};

		@Override
		public IAction getAction(Barcode barcode) {
			if (barcodeTypeMapping.containsKey(barcode)) {
				return barcodeTypeMapping.get(barcode);
			}
			return new NoAction();
		}

	}

	public void objectFound(int teamNumber) {
		log("Own object found, join team #" + teamNumber);
		// Report object found
		getGameRunner().getGame().objectFound();
		// Join team
		getGameRunner().getGame().joinTeam(teamNumber);
		// TODO Start working together
	}

}
