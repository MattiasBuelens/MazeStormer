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
		Point tilePosition = (new LongPoint(goalX, goalY)).toPoint().add(new Point(0.5f, 0.5f));
		Point absolutePosition = getMaze().toAbsolute(getMaze().fromTile(tilePosition));
		getRobot().getPoseProvider().setPose(new Pose(absolutePosition.x, absolutePosition.y, 90f));
		getRobot().getPilot().travel(0d);
	}
	
	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}
	
	private IMaze getMaze() {
		return getMainController().getMaze();
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
