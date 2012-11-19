package mazestormer.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import lejos.geom.Point;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Edge;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.Pilot;

public class ExplorerController extends SubController implements
		IExplorerController {

	public ExplorerController(MainController mainController) {
		super(mainController);
	}

	private Pilot getPilot() {
		return getMainController().getRobot().getPilot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
	}

	private Pose getPose() {
		return getMainController().getRobot().getPoseProvider().getPose();
	}
	
	private RangeScanner getRangeScanner(){
		return getMainController().getRobot().getRangeScanner();
	}

	@Override
	public void startExploring() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopExploring() {
		// TODO Auto-generated method stub

	}

	private class ExplorerRunner implements Runnable {

		@Override
		public void run() {
			// 1. QUEUE <-- path only containing the root;
			Stack<Tile> queue = new Stack<Tile>();
			
			Pose startPose = getPose();
			Pose relativeStartPose = getMaze().toRelative(startPose);
			Point startPoint = new Point(relativeStartPose.getX(),relativeStartPose.getY());
			Point startPointTC = getMaze().toTile(startPoint);
			Tile startTile = getMaze().getTileAt(startPointTC);
			
			queue.push(startTile);
			// 2. WHILE QUEUE is not empty
			Tile currentTile,neighborTile;
			ArrayList<Tile> paths = new ArrayList<Tile>();
			while(!queue.empty()){
				currentTile = queue.pop(); // DO remove the first path from the QUEUE (This is the tile the robot is currently on, because of peek and drive)
				
				//scannen en updaten
				scanAndUpdate(queue, currentTile);
				
				// create new paths (to all children);
				selectTiles(queue, currentTile);
				
				// Rijd naar volgende tile (peek)
				queue.peek();
			}
			
			//
			// 3. IF goal reached
			// THEN success;
			// ELSE failure;
		}
		
		//Scans in the direction of UNKNOWN edges, and updates them accordingly
		private void scanAndUpdate(Stack<Tile> givenQueue, Tile givenTile){
			float scanRange = 180f;
			float scanIncrement = 4f;

			int scanCount = (int) (scanRange / scanIncrement) + 1;
			float[] scanAngles = new float[scanCount];
			float scanStart = -scanRange / 2f;
			for (int i = 0; i < scanCount; i++) {
				scanAngles[i] = scanStart + i * scanIncrement;
			}
			getRangeScanner().setAngles(scanAngles);
			
			RangeFeatureDetector detector = getMainController().getRobot().getRangeDetector();
			RangeFeature feature = detector.scan();
		}
		
		private float[] getScanAngles(Tile givenTile){
			ArrayList<Float> list = new ArrayList<Float>();
			
			//TODO: pas heading vastzetten als we linefinder gedaan hebben.
			float heading = getPose().getHeading();
			
			for(Orientation direction : Orientation.values()){
				if(givenTile.getEdgeAt(direction).getType() == Edge.EdgeType.UNKNOWN){
					switch(direction){
					case EAST:
						list.add(new Float(-90f - heading));
						break;
					case NORTH:
						list.add(new Float(0f - heading));
						break;
					case SOUTH:
						list.add(new Float(-180f - heading));
						break;
					case WEST:
						list.add(new Float(90f - heading));
						break;
					}
				}
			}
			
			Float[] angles = (Float[])list.toArray();
			//TODO:
			return null;
		}
		
		//Adds tiles to the queue if the edge in its direction is open and it is not explored yet
		private void selectTiles(Stack<Tile> queue, Tile givenTile){
			Tile neighborTile;
			
			for(Orientation direction : Orientation.values()){
				if(givenTile.getEdgeAt(direction).getType() == Edge.EdgeType.OPEN){
					neighborTile = getMaze().getNeighbor(givenTile, direction);
					// reject the new paths with loops;
					if(!neighborTile.isExplored()){
						// add the new paths to front of QUEUE;
						queue.add(neighborTile);
					}
				}
			}
		}
		
		//Drives to the given tile
		private void driveTo(Tile tile){
			//Matthias
		}
		
		//Drives from the first to the last tile in path
		private void drivePath(ArrayList<Tile> path){
			
		}
		
		

	}

}
