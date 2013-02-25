package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class RotateClockwiseAction implements IAction {

	@Override
	public Future<?> performAction(ControllableRobot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
		robot.getPilot().stop();
		return robot.getPilot().rotateComplete(-360);
	}

}
