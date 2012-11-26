package mazestormer.controller;

import java.util.ArrayList;
import java.util.Stack;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.controller.ExplorerEvent.EventType;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Edge;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.Pilot;

public class ExplorerController extends SubController implements
		IExplorerController {

	private ExplorerRunner runner;

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

	private RangeScanner getRangeScanner() {
		return getMainController().getRobot().getRangeScanner();
	}

	private void postState(EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		runner = new ExplorerRunner();
		runner.start();
	}

	@Override
	public void stopExploring() {
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}

	private class ExplorerRunner implements Runnable {
		private boolean isRunning = false;
		private final Pilot pilot;

		public void start() {
			isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				isRunning = false;
				pilot.stop();
				postState(EventType.STOPPED);
			}
		}

		public ExplorerRunner() {
			this.pilot = getPilot();
		}

		public synchronized boolean isRunning() {
			return isRunning;
		}

		@Override
		public void run() {
			// 1. QUEUE <-- path only containing the root;
			Stack<Tile> queue = new Stack<Tile>();

			Pose startPose = getPose();
			Pose relativeStartPose = getMaze().toRelative(startPose);
			Point startPoint = new Point(relativeStartPose.getX(),
					relativeStartPose.getY());
			Point startPointTC = getMaze().toTile(startPoint);
			Tile startTile = getMaze().getTileAt(startPointTC);

			queue.push(startTile);
			// 2. WHILE QUEUE is not empty
			Tile currentTile, neighborTile;
			ArrayList<Tile> paths = new ArrayList<Tile>();

			while (!queue.empty()) {
				currentTile = queue.pop(); // DO remove the first path from the
											// QUEUE (This is the tile the robot
											// is currently on, because of peek
											// and drive)

				// scannen en updaten
				scanAndUpdate(queue, currentTile);
				currentTile.isExplored();
				// create new paths (to all children);
				selectTiles(queue, currentTile);

				// Rijd naar volgende tile (peek)
				// queue.peek();
			}

			//
			// 3. IF goal reached
			// THEN success;
			// ELSE failure;
		}

		// Scans in the direction of UNKNOWN edges, and updates them accordingly
		private void scanAndUpdate(Stack<Tile> givenQueue, Tile givenTile) {
			getRangeScanner().setAngles(getScanAngles(givenTile));

			RangeFeatureDetector detector = getMainController().getRobot()
					.getRangeDetector();
			detector.setMaxDistance(28);//TODO: juist?
			RangeFeature feature = detector.scan();

			if (feature == null){
				for(Edge currentEdge : givenTile.getEdges()){
					currentEdge.setType(EdgeType.OPEN);
				}
				//TODO: queue
				return;
			}
			
			Orientation absolute;
			for (RangeReading reading : feature.getRangeReadings()) {
				absolute = toAbsolute(angleToOrientation(reading.getAngle()),getPose().getHeading());
				givenTile.getEdgeAt(absolute).setType(EdgeType.WALL);
			}
		}

		private float[] getScanAngles(Tile givenTile) {
			ArrayList<Float> list = new ArrayList<Float>();

			// TODO: pas heading vastzetten als we linefinder gedaan hebben.
			float heading = getPose().getHeading();

			for (Orientation direction : Orientation.values()) {
				if (givenTile.getEdgeAt(direction).getType() == Edge.EdgeType.UNKNOWN) {
					switch (direction) {
					case WEST:
						list.add(new Float(90f - heading));
						break;
					case NORTH:
						list.add(new Float(0f - heading));
						break;
					case EAST:
						list.add(new Float(-90f - heading));
						break;
					case SOUTH:
						list.add(new Float(-180f - heading));
						break;
					}
				}
			}

			float[] floatList = new float[list.size()];
			for (int i = 0; i < list.size(); i++) {
				floatList[i] = list.get(i);
			}

			return floatList;
		}

		// Gives the absolute orientation (in the maze) of a direction, given the heading of the robot
		private Orientation toAbsolute(Orientation direction, float heading){
			float angle = 0;
			switch(direction){
			case EAST:
				angle = -90;
				break;
			case SOUTH:
				angle = -180;
				break;
			case WEST:
				angle = 90;
				break;
			}
			
			return angleToOrientation(angle + heading);
		}
		
		private Orientation angleToOrientation(float angle){
			//System.out.println("Angle: " + angle);
			if(angle > - 45 && angle < 45){
				return Orientation.NORTH;
			} else if(angle >= 45 && angle < 135) {
				return Orientation.WEST;
			} else if(angle >= 135 || angle < -135){
				return Orientation.SOUTH;
			} else {
				return Orientation.EAST;
			}
		}
		// Adds tiles to the queue if the edge in its direction is open and it
		// is not explored yet
		private void selectTiles(Stack<Tile> queue, Tile givenTile) {
			Tile neighborTile;

			for (Orientation direction : Orientation.values()) {
				if (givenTile.getEdgeAt(direction).getType() == Edge.EdgeType.OPEN) {
					neighborTile = getMaze().getOrCreateNeighbor(givenTile,
							direction);
					// reject the new paths with loops;
					if (!neighborTile.isExplored()) {
						// add the new paths to front of QUEUE;
						queue.add(neighborTile);
					}
				}
			}
		}

		// Drives to the given tile
		private void driveTo(Tile start, Tile end) {
			// Matthias
		}

		// Drives from the first to the last tile in path
		private void drivePath(ArrayList<Tile> path) {

		}

	}

}
