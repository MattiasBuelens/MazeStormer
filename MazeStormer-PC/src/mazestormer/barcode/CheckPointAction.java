package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Maze.Target;
import mazestormer.player.Player;
import mazestormer.robot.Robot;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class CheckPointAction implements IAction {

	@Override
	public Future<?> performAction(Player player) {
		checkNotNull(player);
		Robot robot = player.getRobot();
		checkNotNull(robot);
		Maze maze = player.getMaze();
		checkNotNull(maze);

		// Get absolute robot pose
		Pose pose = robot.getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = maze.toRelative(pose.getLocation());
		Point tilePosition = maze.toTile(relativePosition);
		maze.setTarget(Target.CHECKPOINT, maze.getTileAt(tilePosition));
		
		return new ImmediateFuture<Void>(null);
	}

}
