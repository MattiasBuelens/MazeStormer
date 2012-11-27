package mazestormer.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.google.common.eventbus.Subscribe;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.controller.ExplorerEvent.EventType;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Edge;
import mazestormer.maze.Edge.EdgeType;
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

	boolean cont = false;

	@Subscribe
	public void onActionEvent(mazestormer.controller.ActionEvent e) {
		if (e.getEventType() == mazestormer.controller.EventType.STOPPED) {
			cont = true;
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
			ArrayDeque<Tile> queue = new ArrayDeque<Tile>();

			Pose startPose = getPose();
			Pose relativeStartPose = getMaze().toRelative(startPose);
			Point startPoint = new Point(relativeStartPose.getX(),
					relativeStartPose.getY());
			Point startPointTC = getMaze().toTile(startPoint);
			Tile startTile = getMaze().getTileAt(startPointTC);

			System.out.println(startTile);
			
			queue.addLast(startTile);
			// 2. WHILE QUEUE is not empty
			Tile currentTile, nextTile;

			while (!queue.isEmpty()) {
				System.out.println("Queue size: " + queue.size());
				currentTile = queue.pollLast(); // DO remove the first path from the
											// QUEUE
											// (This is the tile the robot
											// is currently on, because of peek
											// and drive)
				
				queue.remove(currentTile);//TODO: weirdness

				// scannen en updaten
				scanAndUpdate(queue, currentTile);
				currentTile.setExplored();
				// create new paths (to all children);
				selectTiles(queue, currentTile);

				// Rijd naar volgende tile (peek)
				if (!queue.isEmpty()) {
					nextTile = queue.peek();
					getMainController().pathFindingControl().startAction(
							nextTile.getX(), nextTile.getY());
					while (!cont) {
					}
				}

				cont = false;
			}
		}

		// Scans in the direction of UNKNOWN edges, and updates them accordingly
		private void scanAndUpdate(ArrayDeque<Tile> givenQueue, Tile givenTile) {
			getRangeScanner().setAngles(getScanAngles(givenTile));

			RangeFeatureDetector detector = getMainController().getRobot()
					.getRangeDetector();
			detector.setMaxDistance(28);// TODO: juist?
			RangeFeature feature = detector.scan();

			if (feature != null) {
				Orientation orientation;
				for (RangeReading reading : feature.getRangeReadings()) {
					orientation = angleToOrientation(reading.getAngle()
							+ getMaze().toRelative(getPose().getHeading()));
					// System.out.println("Wall in " + orientation);
					getMaze().setEdge(givenTile.getPosition(), orientation,
							EdgeType.WALL);
				}
			}

			Tile nbTile;
			for (Edge currentEdge : givenTile.getEdges()) {
				if (currentEdge.getType() == EdgeType.UNKNOWN) {
					// System.out.println("Open in "
					// +
					// currentEdge.getOrientationFrom(givenTile.getPosition()));
					getMaze().setEdge(
							givenTile.getPosition(),
							currentEdge.getOrientationFrom(givenTile
									.getPosition()), EdgeType.OPEN);
					nbTile = getMaze().getOrCreateNeighbor(
							givenTile,
							currentEdge.getOrientationFrom(givenTile
									.getPosition()));
					givenQueue.push(nbTile);

				}
			}
		}

		private float[] getScanAngles(Tile givenTile) {
			ArrayList<Float> list = new ArrayList<Float>();
			// TODO: pas heading vastzetten als we linefinder gedaan hebben.
			float heading = getPose().getHeading();

			for (Orientation direction : Orientation.values()) {
				if (givenTile.getEdgeAt(direction).getType() == EdgeType.UNKNOWN) {
					switch (direction) {
					case WEST:
						list.add(new Float(180f - heading));
						break;
					case NORTH:
						list.add(new Float(90f - heading));
						break;
					case EAST:
						list.add(new Float(0f - heading));
						break;
					case SOUTH:
						list.add(new Float(-90f - heading));
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

		// Pre: We verwachten angle tussen -360 en 360 graden
		private Orientation angleToOrientation(float angle) {
			if (angle > 180)
				angle -= 360f;
			if (angle < -180)
				angle += 360f;

			if (angle > -45 && angle <= 45)
				return Orientation.EAST;
			if (angle > 45 && angle <= 135)
				return Orientation.NORTH;
			if (angle > 135 || angle <= -135)
				return Orientation.WEST;
			return Orientation.SOUTH;
		}

		// Adds tiles to the queue if the edge in its direction is open and it
		// is not explored yet
		private void selectTiles(ArrayDeque<Tile> queue, Tile givenTile) {
			Tile neighborTile;

			for (Orientation direction : Orientation.values()) {
				if (givenTile.getEdgeAt(direction).getType() == Edge.EdgeType.OPEN) {
					neighborTile = getMaze().getOrCreateNeighbor(givenTile,
							direction);
					// reject the new paths with loops;
					if (!neighborTile.isExplored()) {
						// add the new paths to front of QUEUE;
						queue.addLast(neighborTile);
					}
				}
			}
		}
	}

}
