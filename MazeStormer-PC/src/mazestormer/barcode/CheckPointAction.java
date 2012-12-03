package mazestormer.barcode;

import static com.google.common.base.Preconditions.checkNotNull;
import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;

public class CheckPointAction implements IAction{

	@Override
	public void performAction(Robot robot, Maze maze) {
		checkNotNull(robot);
		checkNotNull(maze);
		
		// Get absolute robot pose
		Pose pose = robot.getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = maze.toRelative(pose.getLocation());
		Point tilePosition = maze.toTile(relativePosition);
		maze.setCheckPointTile(maze.getTileAt(tilePosition));
	}
}
