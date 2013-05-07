package mazestormer.command.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.action.AbstractSeesawAction;
import mazestormer.barcode.action.BarcodeMapping;
import mazestormer.barcode.action.IAction;
import mazestormer.barcode.action.NoAction;
import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.util.Future;

public class DriveToRandomTileControlMode extends ControlMode {

	/*
	 * All tiles in the maze;
	 */
	private final List<Tile> tiles = new ArrayList<>();

	/*
	 * Constructor
	 */

	public DriveToRandomTileControlMode(Player player, Commander commander) {
		super(player, commander);
		Collection<Tile> exploredTiles = player.getMaze().getExploredTiles();
		for (Tile exploredTile : exploredTiles) {
			if (!exploredTile.isSeesaw() && !exploredTile.hasBarcode()) {
				this.tiles.add(exploredTile);
			}
		}
	}

	/*
	 * ControlMode Management
	 */

	@Override
	public void takeControl() {
		log("Driving to a random place, because partner is still unknown.");
	}

	@Override
	public void releaseControl() {
		// Nothing to do here.
	}

	/*
	 * Driver support
	 */

	@Override
	public Tile nextTile(Tile currentTile) {
		return tiles.get(new Random().nextInt(tiles.size()));
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	@Override
	public IAction getAction(Barcode barcode) {
		return mapping.getAction(barcode);
	}

	private GameRunner getGameRunner() {
		return (GameRunner) getCommander();
	}

	/*
	 * Utilities
	 */
	
	private final BarcodeMapping mapping = new DriveToRandomTileMapping();

	private class DriveToRandomTileMapping implements BarcodeMapping {

		private final Map<Barcode, IAction> barcodeMapping = new HashMap<Barcode, IAction>() {
			private static final long serialVersionUID = 1L;
			{
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
			if (barcodeMapping.containsKey(barcode)) {
				return barcodeMapping.get(barcode);
			}
			return new NoAction();
		}

	}

	private class SeesawAction extends AbstractSeesawAction {

		private final Barcode seesawBarcode;

		protected SeesawAction(Barcode seesawBarcode) {
			super(DriveToRandomTileControlMode.this.getPlayer(),
					DriveToRandomTileControlMode.this.getCommander()
							.getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
			if (isInternal(getMaze(), seesaw)) {
				if (canDriveOverSeesaw()) {
					// Drive over seesaw
					log("Drive over seesaw");
					seesaw.setOpen(seesawBarcode);
					return driveOverSeesaw(getGameRunner().getGame());
				} else {
					// Drive around
					log("Go around seesaw");
					seesaw.setClosed(seesawBarcode);
				}
			}

			// Skip seesaw tile
			skipCurrentBarcode(true);
			return null;
		}


		protected void skipCurrentBarcode(boolean skip) {
			getCommander().getDriver().skipCurrentBarcode(skip);
		}

	}

	protected void skipCurrentBarcode(boolean skip) {
		getCommander().getDriver().skipCurrentBarcode(skip);
	}

}
