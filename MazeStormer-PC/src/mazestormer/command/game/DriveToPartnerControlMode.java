package mazestormer.command.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * Seesaws to avoid, because they're closed
	 */
	private final Set<Seesaw> seesawsToAvoid = new HashSet<>();

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
		Pose partnerPose = absolutePartner.getRobot().getPoseProvider()
				.getPose();
		// Partner tile
		Tile partnerTile = getPathFinder().getTileAt(partnerPose.getLocation());
		if (currentTile.isNeighbourTo(partnerTile)) {
			log("Standing next to the partner, you've won the game!");
			getGame().win();
			return null;
		}

		// Go to the first tile of the shortest path
		List<Tile> path = getPathFinder().findTilePathWithoutSeesaws(
				currentTile, partnerTile, seesawsToAvoid);
		if (path.isEmpty())
			return null;
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
			super(DriveToPartnerControlMode.this.getPlayer(),
					DriveToPartnerControlMode.this.getCommander().getDriver());
			this.seesawBarcode = seesawBarcode;
		}

		@Override
		public Future<?> performAction(Player player) {
			Seesaw seesaw = getMaze().getSeesaw(seesawBarcode);
			// if (canDriveOverSeesaw()) {
			// log("Drive over seesaw");
			// return driveOverSeesaw(getGame());
			// } else {
			// Drive around
			log("Go around seesaw");
			//seesaw.setClosed(seesawBarcode);
			// Go around seesaw
			seesawsToAvoid.add(getMaze().getSeesaw(seesawBarcode));

			// Skip seesaw tile
			skipCurrentBarcode(true);
			return new NoAction().performAction(player);
			// }
		}

		protected void skipCurrentBarcode(boolean skip) {
			getCommander().getDriver().skipCurrentBarcode(skip);
		}

	}

}
