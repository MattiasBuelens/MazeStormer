package mazestormer.controller;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.pathfinding.AstarSearchAlgorithm;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.robotics.pathfinding.GridNode;
import lejos.robotics.pathfinding.Node;
import lejos.robotics.pathfinding.Path;
import mazestormer.maze.Tile;
import mazestormer.robot.Robot;

public class PathFindingController extends SubController implements IPathFindingController{

	public PathFindingController(MainController mainController){
		super(mainController);
	}

	private Robot getRobot(){
		return getMainController().getRobot();
	}

	@Override
	public void startAction(String action){
		
	}

	@Override
	public void stopAction(){
		
	}
	
	private Path findPath(Tile startTile, Tile goalTile){
		float gridspace = getMainController().getMaze().getTileSize();
		float clearance = getMainController().getMaze().getTileSize()/2;
		Line[] lines = getMainController().getMaze().getLines().toArray(new Line[0]);
		LineMap map = new LineMap(lines, getMainController().getMaze().getBoundingRectangle());
		FourWayGridMesh mesh = new FourWayGridMesh(map, gridspace, clearance);
		
		Node startNode = new GridNode(startTile.getX()+getMainController().getMaze().getTileSize()/2, startTile.getY()+getMainController().getMaze().getTileSize()/2, gridspace);
		Node goalNode = new GridNode(goalTile.getX()+getMainController().getMaze().getTileSize()/2, goalTile.getY()+getMainController().getMaze().getTileSize()/2, gridspace);
		
		AstarSearchAlgorithm astar = new AstarSearchAlgorithm();
		return astar.findPath(startNode, goalNode);
	}

}
