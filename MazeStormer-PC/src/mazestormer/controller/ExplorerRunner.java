package mazestormer.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.maze.Edge;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.PathRunner;
import mazestormer.robot.Robot;

import com.google.common.primitives.Floats;

class ExplorerRunner extends PathRunner {

	private Deque<Tile> queue = new ArrayDeque<Tile>();
	private Tile currentTile;
	private Tile nextTile;

	public ExplorerRunner(Robot robot, Maze maze) {
		super(robot, maze);
	}

	@Override
	public void run() {
		// Initialize
		init();

		// Loop
		while (!queue.isEmpty() && isRunning()) {
			cycle();
		}

		// Done
		cancel();
	}

	private void init() {
		// 1. QUEUE <-- path only containing the root

		Pose startPose = getPose();
		Pose relativeStartPose = maze.toRelative(startPose);
		Point startPoint = relativeStartPose.getLocation();
		Point startPointTC = maze.toTile(startPoint);
		Tile startTile = maze.getTileAt(startPointTC);

		queue.addLast(startTile);
	}

	private void cycle() {
		if (queue.isEmpty() || !isRunning())
			return;

		/*
		 * DO remove the first path from the QUEUE.
		 * 
		 * (This is the tile the robot is currently on, because of peek and
		 * drive.)
		 */
		currentTile = queue.pollLast();

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

	/**
	 * Scans in the direction of *unknown* edges, and updates them accordingly.
	 */
	private void scanAndUpdate(Deque<Tile> queue, Tile givenTile) {
		robot.getRangeScanner().setAngles(getScanAngles(givenTile));

		RangeFeatureDetector detector = robot.getRangeDetector();
		RangeFeature feature = detector.scan();

		if (feature != null) {
			Orientation orientation;
			for (RangeReading reading : feature.getRangeReadings()) {
				orientation = angleToOrientation(reading.getAngle()
						+ maze.toRelative(getPose().getHeading()));
				maze.setEdge(givenTile.getPosition(), orientation,
						EdgeType.WALL);
			}
		}

		for (Edge currentEdge : givenTile.getEdges()) {
			if (currentEdge.getType() == EdgeType.UNKNOWN) {
				maze.setEdge(
						givenTile.getPosition(),
						currentEdge.getOrientationFrom(givenTile.getPosition()),
						EdgeType.OPEN);
			}
		}
	}

	/**
	 * Adds tiles to the queue if the edge in its direction is open and it is
	 * not explored yet.
	 */
	private void selectTiles(Deque<Tile> queue, Tile givenTile) {
		Tile neighborTile;

		for (Orientation direction : givenTile.getOpenSides()) {
			neighborTile = maze.getOrCreateNeighbor(givenTile, direction);
			// Reject the new paths with loops
			if (!neighborTile.isExplored() && !queue.contains(neighborTile)) {
				// Add the new paths to front of queue
				queue.addLast(neighborTile);
			}
		}
	}

	/**
	 * Get the angles towards *unknown* edges to scan.
	 */
	private float[] getScanAngles(Tile tile) {
		ArrayList<Float> list = new ArrayList<Float>();
		// TODO: pas heading vastzetten als we linefinder gedaan hebben.
		float heading = getPose().getHeading();

		for (Orientation direction : tile.getUnknownSides()) {
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

		// Sort angles
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

		if (angle > -45 && angle <= 45) {
			return Orientation.EAST;
		} else if (angle > 45 && angle <= 135) {
			return Orientation.NORTH;
		} else if (angle > 135 || angle <= -135) {
			return Orientation.WEST;
		} else {
			return Orientation.SOUTH;
		}
	}

}