package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import static com.google.common.base.Preconditions.*;

public class NoAction implements IAction {

	@Override
	public void performAction(ControllableRobot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
	}
}
