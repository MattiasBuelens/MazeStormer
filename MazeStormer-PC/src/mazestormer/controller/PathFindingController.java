package mazestormer.controller;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.maze.TileSequenceRunner;
import mazestormer.robot.Robot;
import mazestormer.util.LongPoint;

public class PathFindingController extends SubController implements IPathFindingController{
	
	private TileSequenceRunner runner;

	public PathFindingController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}
	
	private Maze getMaze(){
		return getMainController().getMaze();
	}
	
	private Maze getSourceMaze() {
		return getMainController().getSourceMaze();
	}
	
	private void setMaze(Maze maze) {
		getMainController().setMaze(maze);
	}
	
	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	@Override
	public void startAction(long goalX, long goalY) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile);
		this.runner.start();
	}

	@Override
	public void stopAction(){
		if (this.runner != null) {
			this.runner.stop();
			this.runner = null;
		}
	}
	
	@Override
	public long getCurrentTileX() {
		return getCurrentTile().getX();
	}
	
	@Override
	public long getCurrentTileY() {
		return getCurrentTile().getY();
	}
	
	private Tile getCurrentTile() {
		// Get absolute robot pose
		Pose pose = getRobot().getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = getMaze().toRelative(pose.getLocation());
		Point tilePosition = getMaze().toTile(relativePosition);
		return getMaze().getTileAt(tilePosition);
	}

	@Override
	public long getTileMinX() {
		return getMaze().getMinX();
	}

	@Override
	public long getTileMinY() {
		return getMaze().getMinY();
	}

	@Override
	public long getTileMaxX() {
		return getMaze().getMaxX();
	}

	@Override
	public long getTileMaxY() {
		return getMaze().getMaxY();
	}
	
	@Override
	public void addSourceMaze(){
		if (getSourceMaze().getTiles().size() > 1) {
			setMaze(getSourceMaze());
			log("The maze is set to the source maze.");
		} else {
			log("There is no source maze available.");
		}
	}
}
