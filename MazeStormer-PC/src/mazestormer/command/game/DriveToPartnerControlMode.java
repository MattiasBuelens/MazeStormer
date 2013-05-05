package mazestormer.command.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lejos.robotics.navigation.Pose;
import mazestormer.barcode.Barcode;
import mazestormer.barcode.action.AbstractSeesawAction;
import mazestormer.barcode.action.BarcodeMapping;
import mazestormer.barcode.action.IAction;
import mazestormer.barcode.action.NoAction;
import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.game.Game;
import mazestormer.maze.Seesaw;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.util.Future;

public class DriveToPartnerControlMode extends ControlMode {

	/*
	 * Mapping
	 */
	private final BarcodeMapping mapping = new DriveToPartnerMapping();

	/*
	 * Constructor
	 */

	public DriveToPartnerControlMode(Player player, Commander commander) {
		super(player, commander);
	}

	/*
	 * ControlMode Management
	 */

	@Override
	public void takeControl() {
		log("Driving to the designated partner, winning!");
	}

	@Override
	public void releaseControl() {
		// should never happen.
	}

	/*
	 * Driver support
	 */

	@Override
	public Tile nextTile(Tile currentTile) {
		if (!hasAbsolutePartner()) {
			log("Something went wrong, trying to drive to your partner, but there is no partner yet.");
			return null;
		}

		/*
		 * The shortest path is recalculated tile per tile, as we have no idea
		 * where our partner will be going.
		 */

		Player absolutePartner = getAbsolutePartner();
		// Partner position in own coordinate system
		Pose partnerPose = absolutePartner.getRobot().getPoseProvider().getPose();
		// Partner tile
		Tile partnerTile = getPathFinder().getTileAt(partnerPose.getLocation());
		if (currentTile.isNeighbourTo(partnerTile)) {
			log("Standing next to the partner, you've won the game!");
			getGame().win();
			return null;
		}

		// Go to the first tile of the shortest path
		List<Tile> path = createPath(currentTile, partnerTile);
		return path.get(0);
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return true;
	}

	@Override
	public IAction getAction(Barcode barcode) {
		return mapping.getAction(barcode);
	}

	/*
	 * Utilities
	 */

	private Game getGame() {
		return ((GameRunner) getCommander()).getGame();
	}

	private Player getAbsolutePartner() {
		return getGame().getAbsolutePartner();
	}

	private boolean hasAbsolutePartner() {
		return getGame().hasAbsolutePartner();
	}

	private class DriveToPartnerMapping implements BarcodeMapping {

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
			super(DriveToPartnerControlMode.this.getPlayer(), DriveToPartnerControlMode.this.getCommander().getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
			if (meantToCrossSeesaw()) {
				// Check if seesaw is open
				if (canDriveOverSeesaw()) {
					// Drive over seesaw
					log("Drive over seesaw");
					seesaw.setOpen(seesawBarcode);
					return driveOverSeesaw();
				} else {
					// Drive around
					log("Go around seesaw");
					seesaw.setClosed(seesawBarcode);
					// Go around seesaw
					List<Tile> pathAroundSeesaw = getPathWithoutSeesaw(seesaw);
					return redirect(pathAroundSeesaw);
				}
			} else {
				// Not trying to cross
				log("Not trying to cross seesaw, skip to next tile");
			}

			// Skip seesaw tile
			skipToNextTile();
			return null;
		}

		private boolean meantToCrossSeesaw() {
			return !(getDriver().getCurrentTile().equals(getDriver().getStartTile()));
		}

		private void skipToNextTile() {
			getCommander().getDriver().skipToNextTile();
		}

	}

}
