package mazestormer.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.barcode.SeesawAction;
import mazestormer.explore.Commander;
import mazestormer.explore.ControlMode;
import mazestormer.explore.Driver;
import mazestormer.explore.ExploreControlMode;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

public class FindObjectControlMode extends ControlMode {

	private final ExploreControlMode exploreMode;

	private LinkedList<Tile> reachableSeesawQueue;

	public FindObjectControlMode(Player player, Commander commander) {
		super(player, commander);
		exploreMode = new ExploreControlMode(player, commander);
	}

	@Override
	public void takeControl(Driver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseControl(Driver driver) {
		// TODO Auto-generated method stub

	}

	@Override
	public Tile nextTile(Tile currentTile) {
		// als de queue van de explorecontrolMode niet leeg is, gewoon daaraan
		// opvragen. anders met de reachableSeesawQueue werken, hierin zitten al
		// de barcodeTiles van seesaws. Indien zelfs deze leeg is, ga dan op een
		// T stuk, of ga over naar een volgende fase
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	private ExploreControlMode getExploreControlMode() {
		return exploreMode;
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	private class FindObjectBarcodeMapping implements BarcodeMapping {

		private final Map<Barcode, Class<?>> barcodeTypeMapping = new HashMap<Barcode, Class<?>>() {
			private static final long serialVersionUID = 1L;
			{
				put(new Barcode(0), ObjectFoundAction.class);
				put(new Barcode(1), ObjectFoundAction.class);
				put(new Barcode(2), ObjectFoundAction.class);
				put(new Barcode(3), ObjectFoundAction.class);
				put(new Barcode(4), ObjectFoundAction.class);
				put(new Barcode(5), ObjectFoundAction.class);
				put(new Barcode(6), ObjectFoundAction.class);
				put(new Barcode(7), ObjectFoundAction.class);
				put(new Barcode(11), SeesawAction.class);
				put(new Barcode(13), SeesawAction.class);
				put(new Barcode(15), SeesawAction.class);
				put(new Barcode(17), SeesawAction.class);
				put(new Barcode(19), SeesawAction.class);
				put(new Barcode(21), SeesawAction.class);
			}
		};

		private static final int START_OF_BARCODERANGE = 11;
		private static final int END_OF_BARCODERANGE = 21;

		@Override
		public IAction getAction(Barcode barcode) {
			Class<?> foundBarcodeType = barcodeTypeMapping.get(barcode);
			// objectbarcode
			if (foundBarcodeType.equals(ObjectFoundAction.class)) {
				if (getObjectNumberFromBarcode(barcode) == ((GameRunner) getCommander())
						.getObjectNumber()) {
					// indien eigen barcode: return ObjectBarcodeAction;
					return new ObjectFoundAction(); // eigen voorwerp wordt
													// opgepikt
				}
				// verwijder volgende tegels uit queue

				// voeg info toe aan maze
				getGameRunner().setObjectTile();
			}
			// seesawBarcode
			else if (foundBarcodeType.equals(SeesawAction.class)) {
				// voeg info toe aan maze
				getGameRunner().setSeesawWalls();
				// andere nog te exploreren tegels?
				if (getExploreControlMode().hasUnexploredTiles()) {

					// ja
					// return noAction;
					// driver zal gewoon verder exploreren
					return new NoAction();
				} else {
					// nee
					// is wip open?
					if (!getRobot().getIRSensor().hasReading()) {
						// ja
						// return seesawBarcode; // de seesaw wordt overgestoken
						// en van
						// daar wordt verder geëxploreerd
						reachableSeesawQueue.clear();
						return new SeesawAction();
					} else {
						// nee
						// zijn er andere wippen beschikbaar?
						if (reachableSeesawQueue.isEmpty()) {
							List<Tile> reachableTiles = getReachableSeesawBarcodeTiles(barcode);
							if (!reachableTiles.isEmpty()) {

								// ja
								// rijd naar de volgende wip zijn barcode-tegel
								reachableSeesawQueue.addAll(reachableTiles);
							}

							// nee
							// rijd naar een T of Cross -stuk en wacht tot er
							// iemand
							// passeert
							return new NoAction();
						}
					}
				}
			}
			return new NoAction();
		}

		private int getObjectNumberFromBarcode(Barcode objectBarcode) {
			return (objectBarcode.getValue() % 4);
		}

		private List<Tile> getReachableSeesawBarcodeTiles(Barcode barcode) {
			List<Tile> reachableTiles = new ArrayList<>();
			PathFinder pf = new PathFinder(getMaze());
			for (Tile tile : getMaze().getBarcodeTiles()) {
				Barcode tileBarcode = tile.getBarcode();
				int number = tileBarcode.getValue();
				if (number >= START_OF_BARCODERANGE
						&& number <= END_OF_BARCODERANGE
						&& !tileBarcode.equals(barcode)
						&& !tileBarcode.equals(Seesaw.getOtherBarcode(barcode))
						&& !pf.findPathWithoutSeesaws(
								getGameRunner().getCurrentTile(), tile)
								.isEmpty() && otherSideUnexplored(tile)) {
					reachableTiles.add(tile);
				}
			}
			Collections.sort(reachableTiles,
					getExploreControlMode().new ClosestTileComparator(
							getGameRunner().getCurrentTile()));
			reachableTiles.add(getMaze().getBarcodeTile(barcode));
			return reachableTiles;
		}

		private boolean otherSideUnexplored(Tile seesawBarcodeTile) {
			Barcode barcode = seesawBarcodeTile.getBarcode();
			Tile otherBarcodeTile = getMaze()
					.getOtherSeesawBarcodeTile(barcode);
			for (Orientation orientation : Orientation.values()) {
				if (getMaze().getNeighbor(otherBarcodeTile, orientation)
						.isSeesaw())
					return !getMaze().getNeighbor(otherBarcodeTile,
							orientation.rotateClockwise(2)).isExplored();
			}
			return false;
		}

		private int getTeamNumberFromBarcode(Barcode objectBarcode) {
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		// TODO
		return null;
	}

}
