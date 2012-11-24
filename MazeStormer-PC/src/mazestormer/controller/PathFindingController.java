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

	public PathFindingController(MainController mainController){
		super(mainController);
	}

	private Robot getRobot(){
		return getMainController().getRobot();
	}
	
	private Maze getMaze(){
		return getMainController().getMaze();
	}

	@Override
	public void startAction(long goalX, long goalY){
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
	public long getCurrentTileX(){
		return getCurrentTile().getX();
	}
	
	@Override
	public long getCurrentTileY(){
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTileMinY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTileMaxX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTileMaxY() {
		// TODO Auto-generated method stub
		return 0;
	}

}
