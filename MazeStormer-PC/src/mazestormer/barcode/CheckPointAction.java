package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.command.CommandTools;
import mazestormer.maze.IMaze;
import mazestormer.robot.Robot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class CheckPointAction implements IAction {

	@Override
	public Future<?> performAction(CommandTools player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		IMaze maze = player.getMaze();
		checkNotNull(maze);

		// Get absolute robot pose
		Pose pose = robot.getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = maze.toRelative(pose.getLocation());
		Point tilePosition = maze.toTile(relativePosition);
		maze.setTarget(mazestormer.maze.IMaze.Target.CHECKPOINT, maze.getTileAt(tilePosition));
		
		return new ImmediateFuture<Void>(null);
	}

}
