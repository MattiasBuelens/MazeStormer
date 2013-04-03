package mazestormer.controller;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.LongPoint;

public class CheatController extends SubController implements ICheatController {

	public CheatController(MainController mainController) {
		super(mainController);
	}

	@Override
	public void teleportTo(long goalX, long goalY) {
		// Center on tile
		Point relativePosition = getMaze().getTileCenter(new LongPoint(goalX, goalY));
		Point absolutePosition = getMaze().toAbsolute(relativePosition);
		// Set pose
		Pose pose = new Pose();
		pose.setLocation(absolutePosition);
		getRobot().getPoseProvider().setPose(pose);
		// Trigger pose update
		getRobot().getPilot().travel(0d);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private IMaze getMaze() {
		return getMainController().getPlayer().getMaze();
	}

	private Maze getSourceMaze() {
		return getMainController().getWorld().getMaze();
	}

	@Override
	public long getTileMinX() {
		return getSourceMaze().getMinX();
	}

	@Override
	public long getTileMinY() {
		return getSourceMaze().getMinY();
	}

	@Override
	public long getTileMaxX() {
		return getSourceMaze().getMaxX();
	}

	@Override
	public long getTileMaxY() {
		return getSourceMaze().getMaxY();
	}

}
