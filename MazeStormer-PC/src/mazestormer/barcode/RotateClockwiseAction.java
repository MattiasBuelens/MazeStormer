package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.IMaze;
import mazestormer.player.CommandTools;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class RotateClockwiseAction implements IAction {

	@Override
	public Future<?> performAction(CommandTools player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		IMaze maze = player.getMaze();
		checkNotNull(maze);
		
		robot.getPilot().stop();
		return robot.getPilot().rotateComplete(-360);
	}

}
