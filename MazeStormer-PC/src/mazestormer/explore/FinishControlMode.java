package mazestormer.explore;

import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.BarcodeSpeed;
import mazestormer.maze.IMaze.Target;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;

public class FinishControlMode extends ControlMode {

	public FinishControlMode(Player player, BarcodeMapping mapping) {
		super(player, mapping);
	}

	@Override
	public void takeControl(Driver driver) {
		log("Traveling to checkpoint and goal");
		// Start traveling at high speed
		driver.getRobot().getPilot().setTravelSpeed(BarcodeSpeed.HIGH.getBarcodeSpeedValue());
	}

	@Override
	public void releaseControl(Driver driver) {
		// Reset travel speed
		driver.getRobot().getPilot().setTravelSpeed(ControllableRobot.travelSpeed);
	}

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
