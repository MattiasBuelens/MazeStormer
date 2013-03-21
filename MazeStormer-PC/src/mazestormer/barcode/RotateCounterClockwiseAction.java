package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class RotateCounterClockwiseAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);
		
		robot.getPilot().stop();
		return robot.getPilot().rotateComplete(360);
	}

}
