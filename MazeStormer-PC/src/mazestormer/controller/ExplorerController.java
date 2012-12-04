package mazestormer.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.controller.ExplorerEvent.EventType;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Edge;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.PathRunner;
import mazestormer.robot.Robot;

import com.google.common.primitives.Floats;

public class ExplorerController extends SubController implements
		IExplorerController {

	private ExplorerRunner runner;

	public ExplorerController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
	}

	private RangeScanner getRangeScanner() {
		return getMainController().getRobot().getRangeScanner();
	}

	private void postState(EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		runner = new ExplorerRunner(getRobot(), getMaze());
		runner.start();
	}

	@Override
	public void stopExploring() {
		if (runner != null) {
			runner.cancel();
			runner = null;
		}
	}

	private class ExplorerRunner extends PathRunner {

		private Deque<Tile> queue = new ArrayDeque<Tile>();

		public ExplorerRunner(Robot robot, Maze maze) {
			super(robot, maze);
		}

		@Override
		public void onStarted() {
			super.onStarted();
			postState(EventType.STARTED);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			postState(EventType.STOPPED);
		}

		// TODO Refactor run() into cycle()
		private void cycle() {
			if (queue.isEmpty() || !isRunning())
				return;

		}

		@Override
		public void run() {
			// 1. QUEUE <-- path only containing the root;

			Pose startPose = getPose();
			Pose relativeStartPose = getMaze().toRelative(startPose);
			Point startPoint = relativeStartPose.getLocation();
			Point startPointTC = getMaze().toTile(startPoint);
			Tile startTile = getMaze().getTileAt(startPointTC);

			// System.out.println(startTile);

			queue.addLast(startTile);
			// 2. WHILE QUEUE is not empty
			Tile currentTile, nextTile;

			while (!queue.isEmpty() && isRunning()) {
				currentTile = queue.pollLast(); // DO remove the first path from
												// the
				// QUEUE
				// (This is the tile the robot
				// is currently on, because of peek
				// and drive)

				// scannen en updaten
				scanAndUpdate(queue, currentTile);
				currentTile.setExplored();
				// create new paths (to all children);
				selectTiles(queue, currentTile);

				// Rijd naar volgende tile (peek)
				if (!queue.isEmpty()) {
					nextTile = queue.peekLast();
					goTo(nextTile);
				}
			}
			stopExploring();
		}

		private void goTo(Tile goal) {
			// Create path
			navigator.clearPath();
			List<Waypoint> waypoints = findPath(goal);
			for (Waypoint waypoint : waypoints) {
				navigator.addWaypoint(waypoint);
			}
			// Follow path
			navigator.followPath();
			// Wait until at end
			while (!navigator.waitForStop())
				Thread.yield();
		}

		// Scans in the direction of UNKNOWN edges, and updates them accordingly
		private void scanAndUpdate(Deque<Tile> queue, Tile givenTile) {
			getRangeScanner().setAngles(getScanAngles(givenTile));

			RangeFeatureDetector detector = getMainController().getRobot()
					.getRangeDetector();
			RangeFeature feature = detector.scan();

			if (feature != null) {
				Orientation orientation;
				for (RangeReading reading : feature.getRangeReadings()) {
					orientation = angleToOrientation(reading.getAngle()
							+ getMaze().toRelative(getPose().getHeading()));
					getMaze().setEdge(givenTile.getPosition(), orientation,
							EdgeType.WALL);
				}
			}

			for (Edge currentEdge : givenTile.getEdges()) {
				if (currentEdge.getType() == EdgeType.UNKNOWN) {
					getMaze().setEdge(
							givenTile.getPosition(),
							currentEdge.getOrientationFrom(givenTile
									.getPosition()), EdgeType.OPEN);
				}
			}
		}

		private float[] getScanAngles(Tile givenTile) {
			ArrayList<Float> list = new ArrayList<Float>();
			// TODO: pas heading vastzetten als we linefinder gedaan hebben.
			float heading = getPose().getHeading();

			for (Orientation direction : givenTile.getUnknownSides()) {
				// TODO Check if this replacement is equivalent
				// float angle = Orientation.EAST.angleTo(direction);
				// angle = normalize(angle - heading);

				switch (direction) {
				case WEST:
					list.add(normalize(180f - heading));
					break;
				case NORTH:
					list.add(normalize(90f - heading));
					break;
				case EAST:
					list.add(normalize(0f - heading));
					break;
				case SOUTH:
					list.add(normalize(-90f - heading));
					break;
				}
			}

			Collections.sort(list);
			return Floats.toArray(list);
		}

		private float normalize(float angle) {
			while (angle > 180)
				angle -= 360f;
			while (angle < -180)
				angle += 360f;
			return angle;
		}

		private Orientation angleToOrientation(float angle) {
			angle = normalize(angle);

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
		private void selectTiles(Deque<Tile> queue, Tile givenTile) {
			Tile neighborTile;

			for (Orientation direction : givenTile.getOpenSides()) {
				neighborTile = getMaze().getOrCreateNeighbor(givenTile,
						direction);
				// reject the new paths with loops;
				if (!neighborTile.isExplored() && !queue.contains(neighborTile)) {
					// add the new paths to front of QUEUE;
					queue.addLast(neighborTile);
				}
			}
		}
	}

}
