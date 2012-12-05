package mazestormer.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.BarcodeRunnerListener;
import mazestormer.maze.Barcode;
import mazestormer.maze.Edge;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.Navigator;
import mazestormer.robot.NavigatorListener;
import mazestormer.robot.PathRunner;
import mazestormer.robot.Robot;
import mazestormer.robot.RunnerListener;

import com.google.common.primitives.Floats;

public class ExplorerRunner extends PathRunner implements NavigationListener {

	private Deque<Tile> queue = new ArrayDeque<Tile>();
	private Tile currentTile;
	private Tile nextTile;

	private final LineFinderRunner lineFinder;
	private final BarcodeRunner barcodeRunner;

	private static final int findLineInterval = 10;
	private int nextFindLine;
	private Barcode nextBarcode;

	public ExplorerRunner(Robot robot, Maze maze) {
		super(robot, maze);

		// Navigator
		navigator.singleStep(true);
		navigator.addNavigationListener(this);

		// Runners
		lineFinder = new LineFinderRunner(robot) {
			@Override
			protected void log(String message) {
				ExplorerRunner.this.log(message);
			}
		};
		lineFinder.addListener(new LineFinderListener());
		barcodeRunner = new BarcodeRunner(robot, maze) {
			@Override
			protected void log(String message) {
				ExplorerRunner.this.log(message);
			}
		};
		barcodeRunner.addBarcodeListener(new BarcodeListener());
	}

	protected void log(String message) {
		System.out.println(message);
	}

	@Override
	public void run() {
		init();
		cycle();
	}

	@Override
	public boolean cancel() {
		// Stop
		getPilot().stop();
		boolean isCancelled = super.cancel();
		// Cancel runners
		lineFinder.cancel();
		barcodeRunner.cancel();
		return isCancelled;
	}

	private void init() {
		nextFindLine = 1;
		nextBarcode = null;
		// 1. QUEUE <-- path only containing the root
		Pose startPose = getPose();
		Pose relativeStartPose = maze.toRelative(startPose);
		Point startPoint = relativeStartPose.getLocation();
		Point startPointTC = maze.toTile(startPoint);
		Tile startTile = maze.getTileAt(startPointTC);

		queue.addLast(startTile);
	}

	private void cycle() {
		if (queue.isEmpty() || !isRunning()) {
			resolve();
			return;
		}

		// Remove the first path from the queue
		// This is the current tile of the robot
		currentTile = queue.pollLast();

		log("Scanning " + currentTile.getPosition());
		scanAndUpdate(currentTile);
		currentTile.setExplored();
		throwWhenCancelled();

		// Set and consume barcode
		if (nextBarcode != null) {
			maze.setBarcode(currentTile.getPosition(), nextBarcode);
			nextBarcode = null;
		}

		// Create new paths to all neighbors
		selectTiles(currentTile);

		// Destination reached
		if (queue.isEmpty()) {
			resolve();
			return;
		}

		// Go to next tile
		nextTile = queue.peekLast();
		goTo(nextTile);
	}

	private void goTo(Tile goal) {
		// Create path
		navigator.clearPath();
		List<Waypoint> waypoints = findPath(goal);
		for (Waypoint waypoint : waypoints) {
			navigator.addWaypoint(waypoint);
		}

		navigator.followPathUntil(Navigator.State.TRAVEL);
	}

	private synchronized void beforeTravel() {
		if (shouldFindLine()) {
			// Start line finder
			lineFinder.start();
		} else {
			afterLineFinder();
		}
	}

	private synchronized void travel() {
		// Follow path until way point is reached
		navigator.stop();
		navigator.resumeFromUntil(Navigator.State.TRAVEL,
				Navigator.State.NEXT_STEP);
	}

	private synchronized void nextStep() {
		// Increment counter for line finder
		nextFindLine = (nextFindLine + 1) % findLineInterval;
		log("After travel: " + nextFindLine);
		// Next step
		cycle();
	}

	private synchronized void beforeLineFinder() {
	}

	private synchronized void afterLineFinder() {
		// Cancel line finder if still running
		lineFinder.cancel();
		// Travel to way point
		travel();
		// Start barcode runner
		if (!currentTile.hasBarcode()) {
			barcodeRunner.start();
		}
	}

	private synchronized void beforeBarcode() {
		log("Before barcode, pose:" + getPose());
		// Stop navigator
		navigator.stop();
		// Cancel line finder if still running
		lineFinder.cancel();
	}

	private synchronized void afterBarcode(byte barcode) {
		log("After barcode, pose:" + getPose());
		// Store barcode
		nextBarcode = new Barcode(barcode);
		// Travel to way point
		navigator.stop();
		travel(-barcodeRunner.getBarcodeLength() / 2);
		travel();
	}

	/**
	 * Scans in the direction of *unknown* edges, and updates them accordingly.
	 */
	private void scanAndUpdate(Tile tile) {
		RangeFeature feature = robot.getRangeDetector().scan(
				getScanAngles(tile));

		// Place walls
		if (feature != null) {
			Orientation orientation;
			for (RangeReading reading : feature.getRangeReadings()) {
				orientation = angleToOrientation(reading.getAngle()
						+ maze.toRelative(getPose().getHeading()));
				maze.setEdge(tile.getPosition(), orientation, EdgeType.WALL);
			}
		}

		// Replace unknown edges with openings
		for (Edge edge : tile.getEdges()) {
			if (edge.getType() == EdgeType.UNKNOWN) {
				maze.setEdge(tile.getPosition(),
						edge.getOrientationFrom(tile.getPosition()),
						EdgeType.OPEN);
			}
		}
	}

	/**
	 * Adds tiles to the queue if the edge in its direction is open and it is
	 * not explored yet.
	 */
	private void selectTiles(Tile tile) {
		for (Orientation direction : tile.getOpenSides()) {
			Tile neighborTile = maze.getOrCreateNeighbor(tile, direction);
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
			// Get angle relative to positive X (east) direction
			float angle = Orientation.EAST.angleTo(direction);
			list.add(normalize(angle - heading));
		}

		// Sort angles
		Collections.sort(list);
		return Floats.toArray(list);
	}

	/**
	 * Check whether the robot should find a line to adjust its position.
	 */
	protected boolean shouldFindLine() {
		return nextFindLine == 0;
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

	private class LineFinderListener implements RunnerListener {
		@Override
		public void onStarted() {
			beforeLineFinder();
		}

		@Override
		public void onCompleted() {
			afterLineFinder();
		}

		@Override
		public void onCancelled() {
		}
	}

	private class BarcodeListener implements BarcodeRunnerListener {

		@Override
		public void onStartBarcode() {
			beforeBarcode();
		}

		@Override
		public void onEndBarcode(byte barcode) {
			afterBarcode(barcode);
		}

	}

	@Override
	public void atWaypoint(Waypoint waypoint, Pose pose, int sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pathComplete(Waypoint waypoint, Pose pose, int sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
		// TODO Auto-generated method stub

	}

}