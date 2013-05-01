package mazestormer.barcode.action;

import static com.google.common.base.Preconditions.checkNotNull;
import mazestormer.maze.IMaze;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.WaitFuture;

public class WaitAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		ControllableRobot robot = (ControllableRobot) player.getRobot();
		checkNotNull(robot);
		IMaze maze = player.getMaze();
		checkNotNull(maze);

		/*
		 * TODO This won't actually prevent the robot from doing anything. Other
		 * threads can still access and operate the robot while this thread is
		 * sleeping.
		 */
		robot.getPilot().stop();

		// Resolve after timeout
		WaitFuture<Void> future = new WaitFuture<Void>();
		future.resolveAfter(null, 5000);
		return future;
	}

}
