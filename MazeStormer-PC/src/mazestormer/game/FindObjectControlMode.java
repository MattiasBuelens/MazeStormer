package mazestormer.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.DriveOverSeesawAction;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.barcode.ObjectFoundAction;
import mazestormer.explore.AbstractExploreControlMode;
import mazestormer.explore.Commander;
import mazestormer.explore.Driver;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.LongPoint;

public class FindObjectControlMode extends AbstractExploreControlMode {

	private FindObjectBarcodeMapping findObjectBarcodeMapping = new FindObjectBarcodeMapping();

	public FindObjectControlMode(Player player, Commander commander) {
		super(player, commander);
	}

	/*
	 * Getters
	 */

	@Override
	public GameRunner getCommander() {
		return (GameRunner) super.getCommander();
	}

	public Game getGame() {
		return getCommander().getGame();
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	/*
	 * Methodes specifiek voor deze controlMode
	 */

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
		return true;
	}

	@Override
	public BarcodeMapping getBarcodeMapping() {
		return findObjectBarcodeMapping;
	}

	/*
	 * Utilities
	 */

	public void setSeesawWalls() {
		log("Seesaw on next tiles, set seesaw and barcode");

		IMaze maze = getMaze();

		Tile currentTile = getDriver().getCurrentTile();
		Tile nextTile = getDriver().getNextTile();
		Orientation orientation = currentTile.orientationTo(nextTile);
		TileShape tileShape = new TileShape(TileType.STRAIGHT, orientation);

		Barcode seesawBarcode = currentTile.getBarcode();
		Barcode otherBarcode = Seesaw.getOtherBarcode(seesawBarcode);

		// Seesaw
		LongPoint nextTilePosition = nextTile.getPosition();
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, seesawBarcode);
		maze.setExplored(nextTilePosition);

		// Seesaw
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setSeesaw(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);

		// Other seesaw barcode
		nextTilePosition = orientation.shift(nextTilePosition);
		maze.setTileShape(nextTilePosition, tileShape);
		maze.setBarcode(nextTilePosition, otherBarcode);
		maze.setExplored(nextTilePosition);
	}

	public void objectFound(int teamNumber) {
		log("Own object found, join team #" + teamNumber);
		// Report object found
		getGame().objectFound();
		// Join team
		getGame().joinTeam(teamNumber);
		// TODO Start working together
	}

	private class SeesawAction implements IAction {

		private Barcode barcode;

		private SeesawAction(Barcode barcode) {
			this.barcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			setSeesawWalls(); // voeg info toe aan maze
			
			// indien er nog te exploreren tegels zijn:
			if (hasUnexploredTiles()) {
				// driver zal gewoon verder exploreren
				return new NoAction().performAction(player);
				
			// indien er geen nog te exploreren tegels zijn (dus eiland/doolhof geëxploreerd):	
			} else {
				// indien de wip bereidbaar is:
				if (!getRobot().getIRSensor().hasReading()) {
					reachableSeesawQueue.clear();
					// de seesaw wordt overgestoken en van daar wordt verder geëxploreerd
					return new DriveOverSeesawAction().performAction(player);

				// indien de wip niet bereidbaar is:
				} else {
					
					// indien nog geen alternatieve wippen zijn gevonden:
					if (reachableSeesawQueue.isEmpty()) { 
						// zoek naar alle bereikbare wippen (incl. huidige)
						List<Tile> reachableTiles = getReachableSeesawBarcodeTiles(barcode);
						// indien er andere wippen bereikbaar zijn:
						if (!reachableTiles.isEmpty()) {
							// voeg de bereikbare wippen toe aan de lijst
							reachableSeesawQueue.addAll(reachableTiles);
							// TODO: rijd naar de eerste wip in de lijst die
							// niet de huidige wip is, geef ook een noAction
							// terug
						}
					}

					else { // indien er wel al alternatieve wippen zijn gevonden

						// nee
						// rijd naar een T of Cross -stuk en wacht tot er
						// iemand
						// passeert
						return new NoAction().performAction(player);
					}
				}
			}
		}
		
		/*
		 * Utilities
		 */

		private List<Tile> getReachableSeesawBarcodeTiles(Barcode barcode) {
			List<Tile> reachableTiles = new ArrayList<>();
			PathFinder pf = new PathFinder(getMaze());
			for (Tile tile : getMaze().getBarcodeTiles()) {
				Barcode tileBarcode = tile.getBarcode();
				int number = tileBarcode.getValue();
				if (number >= FindObjectBarcodeMapping.START_OF_BARCODERANGE
						&& number <= FindObjectBarcodeMapping.END_OF_BARCODERANGE
						&& !tileBarcode.equals(barcode)
						&& !tileBarcode.equals(Seesaw.getOtherBarcode(barcode))
						&& !pf.findPathWithoutSeesaws(
								getGameRunner().getCurrentTile(), tile)
								.isEmpty() && otherSideUnexplored(tile)) {
					reachableTiles.add(tile);
				}
			}
			Collections.sort(reachableTiles, new ClosestTileComparator(
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

	private class FindObjectBarcodeMapping implements BarcodeMapping {

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

		public static final int START_OF_BARCODERANGE = 11;
		public static final int END_OF_BARCODERANGE = 21;

		@Override
		public IAction getAction(Barcode barcode) {
			if(barcodeTypeMapping.containsKey(barcode)){
				return barcodeTypeMapping.get(barcode);
			}
			return new NoAction();
		}

	}

}
