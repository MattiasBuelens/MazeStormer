package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.Robot;
import static com.google.common.base.Preconditions.*;

public class NoAction implements IAction {

	@Override
	public void performAction(Robot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
	}
}
