package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.IMaze;
import mazestormer.player.CommandTools;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class HighSpeedAction implements IAction {

	@Override
	public Future<?> performAction(CommandTools player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		IMaze maze = player.getMaze();
		checkNotNull(maze);
		
		robot.getPilot().setTravelSpeed(BarcodeSpeed.HIGH.getBarcodeSpeedValue());

		return new ImmediateFuture<Void>(null);
	}

}
