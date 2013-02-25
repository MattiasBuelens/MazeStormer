package mazestormer.barcode;

import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;
import static com.google.common.base.Preconditions.*;

public class NoAction implements IAction {

	@Override
	public Future<?> performAction(ControllableRobot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);

		return new ImmediateFuture<Void>(null);
	}
}
