package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class RotateCounterClockwiseAction implements IAction{

	@Override
	public void performAction(Robot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
		robot.getPilot().rotate(360);
	}
}
