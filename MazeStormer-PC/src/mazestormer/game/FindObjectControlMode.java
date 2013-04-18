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
import mazestormer.util.Future;

public class FindObjectControlMode extends ControlMode{
	
	private FindObjectBarcodeMapping findObjectBarcodeMapping = new FindObjectBarcodeMapping();

	private final ExploreControlMode exploreMode;

	private LinkedList<Tile> reachableSeesawQueue;

	public FindObjectControlMode(Player player, Commander commander) {
		super(player, commander);
		exploreMode = new ExploreControlMode(player, commander);
	}
	
	@Override
	public GameRunner getCommander() {
		return (GameRunner) super.getCommander();
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
	
	private class SeesawAction implements IAction {

		@Override
		public Future<?> performAction(Player player) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class ObjectAction extends ObjectFoundAction {
		
		private Barcode barcode;

		private ObjectAction(Barcode barcode) {
			this.barcode = barcode;
		}
		
		@Override
		public Future<?> performAction(Player player) {
			getGameRunner().setObjectTile(); // voeg info toe aan maze
			
			// TODO: verwijder volgende tegels uit queue? Worden ze ooit toegevoegd?
			
			if(getObjectNumberFromBarcode(barcode) == getCommander().getObjectNumber()){ // indien eigen barcode:
				getCommander().objectFound(getTeamNumberFromBarcode(barcode));
				return super.performAction(player); // eigen voorwerp wordt opgepikt
			} else {
				return null; //?
			}
		}
		
		private int getObjectNumberFromBarcode(Barcode objectBarcode) {
			return (objectBarcode.getValue() % 4);
		}
		
		private int getTeamNumberFromBarcode(Barcode objectBarcode) {
			return objectBarcode.getValue() - (objectBarcode.getValue() % 4);
		}
		
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
			if(foundBarcodeType.equals(ObjectFoundAction.class)) { // indien objectbarcode:
				return new ObjectAction(barcode);
			}

			else if (foundBarcodeType.equals(SeesawAction.class)) { // indien seesawBarcode:
				getGameRunner().setSeesawWalls(); // voeg info toe aan maze
				
				if (getExploreControlMode().hasUnexploredTiles()) { // indien er nog te exploreren tegels zijn:
					return new NoAction(); // driver zal gewoon verder exploreren
					
				} else { // indien er geen nog te exploreren tegels zijn (dus eiland/doolhof geëxploreerd):
					if (!getRobot().getIRSensor().hasReading()) { // indien de wip bereidbaar is:
						reachableSeesawQueue.clear();
						return new SeesawAction(); // de seesaw wordt overgestoken en van daar wordt verder geëxploreerd
						
					} else { // indien de wip niet bereidbaar is:
						if (reachableSeesawQueue.isEmpty()) { // indien nog geen alternatieve wippen zijn gevonden:
							List<Tile> reachableTiles = getReachableSeesawBarcodeTiles(barcode); // zoek naar alle bereikbare wippen (incl. huidige)
							if (!reachableTiles.isEmpty()) { // indien er andere wippen bereikbaar zijn:
								reachableSeesawQueue.addAll(reachableTiles); // voeg de bereikbare wippen toe aan de lijst
								// TODO: rijd naar de eerste wip in de lijst die niet de huidige wip is, geef ook een noAction terug
							}
						}
						
						else { // indien er wel al alternatieve wippen zijn gevonden
								
							
							

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

	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		return findObjectBarcodeMapping;
	}

}
