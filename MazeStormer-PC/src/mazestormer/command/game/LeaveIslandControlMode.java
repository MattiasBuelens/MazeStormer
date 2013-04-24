package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.DriveOverSeesawAction;
import mazestormer.barcode.IAction;
import mazestormer.barcode.NoAction;
import mazestormer.command.AbstractExploreControlMode.ClosestTileComparator;
import mazestormer.command.ControlMode;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class LeaveIslandControlMode extends ControlMode {

	private final ControlMode superControlMode;
	private LinkedList<Tile> reachableSeesawQueue;
	private final BarcodeMapping leaveBarcodeMapping = new LeaveIslandBarcodeMapping();

	public LeaveIslandControlMode(Player player, ControlMode superControlMode) {
		super(player, superControlMode.getCommander());
		this.superControlMode = superControlMode;
	}

	/*
	 * Getters
	 */

	private ControlMode getSuperControlMode() {
		return superControlMode;
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getSuperControlMode().getCommander();
	}

	private ControllableRobot getRobot() {
		return (ControllableRobot) getPlayer().getRobot();
	}

	/*
	 * Methodes eigen voor deze controlmode
	 */

	@Override
	public void takeControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public Tile nextTile(Tile currentTile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Barcode-acties en logica
	 */

	@Override
	public IAction getAction(Barcode barcode) {
		return leaveBarcodeMapping.getAction(barcode);
	}

	private class SeesawAction implements IAction {

		private Barcode barcode;

		private SeesawAction(Barcode barcode) {
			this.barcode = barcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			// indien de wip bereidbaar is:
			if (!getRobot().getIRSensor().hasReading()) {
				reachableSeesawQueue.clear();
				// de seesaw wordt overgestoken en van daar wordt verder
				// geëxploreerd
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
			return null;
		}

	}

	private class LeaveIslandBarcodeMapping implements BarcodeMapping {

		public static final int START_OF_BARCODERANGE = 11;
		public static final int END_OF_BARCODERANGE = 21;

		@Override
		public IAction getAction(Barcode barcode) {
			// TODO OMG DO THIS!!!
			return null;
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
			if (number >= LeaveIslandBarcodeMapping.START_OF_BARCODERANGE
					&& number <= LeaveIslandBarcodeMapping.END_OF_BARCODERANGE && !tileBarcode.equals(barcode)
					&& !tileBarcode.equals(Seesaw.getOtherBarcode(barcode))
					&& !pf.findPathWithoutSeesaws(getGameRunner().getCurrentTile(), tile).isEmpty()
					&& otherSideUnexplored(tile)) {
				reachableTiles.add(tile);
			}
		}
		Collections.sort(reachableTiles, new ClosestTileComparator(getGameRunner().getCurrentTile(), getMaze()));
		reachableTiles.add(getMaze().getBarcodeTile(barcode));
		return reachableTiles;
	}

	private boolean otherSideUnexplored(Tile seesawBarcodeTile) {
		Barcode barcode = seesawBarcodeTile.getBarcode();
		Tile otherBarcodeTile = getMaze().getOtherSeesawBarcodeTile(barcode);
		for (Orientation orientation : Orientation.values()) {
			if (getMaze().getNeighbor(otherBarcodeTile, orientation).isSeesaw())
				return !getMaze().getNeighbor(otherBarcodeTile, orientation.rotateClockwise(2)).isExplored();
		}
		return false;
	}

}
