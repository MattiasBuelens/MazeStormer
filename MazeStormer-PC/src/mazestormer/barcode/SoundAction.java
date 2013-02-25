package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class SoundAction implements IAction {

	@Override
	public Future<?> performAction(ControllableRobot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
		robot.getSoundPlayer().playSound();

		return new ImmediateFuture<Void>(null);
	}

}
