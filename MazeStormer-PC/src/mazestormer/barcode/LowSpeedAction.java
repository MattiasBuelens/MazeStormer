package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.command.CommandTools;
import mazestormer.maze.IMaze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class LowSpeedAction implements IAction {

	@Override
	public Future<?> performAction(CommandTools player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		IMaze maze = player.getMaze();
		checkNotNull(maze);
		
		robot.getPilot().setTravelSpeed(BarcodeSpeed.LOW.getBarcodeSpeedValue());

		return new ImmediateFuture<Void>(null);
	}

}
