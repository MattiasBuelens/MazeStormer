package mazestormer.command.explore;

import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.BarcodeSpeed;
import mazestormer.barcode.ExplorerBarcodeMapping;
import mazestormer.barcode.IAction;
import mazestormer.command.Commander;
import mazestormer.command.ControlMode;
import mazestormer.maze.IMaze.Target;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

public class FinishControlMode extends ControlMode {
	
	/*
	 * Data
	 */

	private static final BarcodeMapping mapping = new ExplorerBarcodeMapping();
	
	/*
	 * Constructor
	 */

	public FinishControlMode(Player player, Commander commander) {
		super(player, commander);
	}
	
	/*
	 * ControlMode Management
	 */

	@Override
	public void takeControl() {
		log("Traveling to checkpoint and goal");
		// Start traveling at high speed
		getCommander().getDriver().getRobot().getPilot()
				.setTravelSpeed(BarcodeSpeed.HIGH.getBarcodeSpeedValue());
	}

	@Override
	public void releaseControl() {
		// Reset travel speed
		getCommander().getDriver().getRobot().getPilot()
				.setTravelSpeed(ControllableRobot.travelSpeed);
	}
	
	/*
	 * Driver support
	 */

	@Override
	public Tile nextTile(Tile currentTile) {
		if (isTarget(currentTile, Target.GOAL)) {
			// Done
			return null;
		} else if (isTarget(currentTile, Target.CHECKPOINT)) {
			// Go to goal
			return getMaze().getTarget(Target.GOAL);
		} else {
			// Go to checkpoint
			return getMaze().getTarget(Target.CHECKPOINT);
		}
	}
	
	@Override
	public IAction getAction(Barcode barcode) {
		return mapping.getAction(barcode);
	}
	
	/*
	 * Utilities
	 */

	private boolean isTarget(Tile tile, Target target) {
		Tile targetTile = getMaze().getTarget(target);
		if (targetTile == null)
			return false;
		return tile.getPosition().equals(targetTile.getPosition());
	}

	@Override
	public boolean isBarcodeActionEnabled() {
		return false;
	}

}
