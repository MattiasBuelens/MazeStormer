package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Robot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class LowSpeedAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
		
		robot.getPilot().setTravelSpeed(BarcodeSpeed.LOW.getBarcodeSpeedValue());

		return new ImmediateFuture<Void>(null);
	}

}
