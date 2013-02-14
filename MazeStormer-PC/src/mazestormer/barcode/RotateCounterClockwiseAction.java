package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;

public class RotateCounterClockwiseAction implements IAction{

	@Override
	public void performAction(ControllableRobot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
		robot.getPilot().rotate(360);
	}
}
