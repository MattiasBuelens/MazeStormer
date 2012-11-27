package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import lejos.util.Delay;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class WaitAction implements IAction {

	@Override
	public void performAction(Robot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);

		/*
		 * TODO This won't actually prevent the robot from doing anything. Other
		 * threads can still access and operate the robot while this thread is
		 * sleeping.
		 */
		robot.getPilot().stop();
		Delay.msDelay(5000);
	}
}
