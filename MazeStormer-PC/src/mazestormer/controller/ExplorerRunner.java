package mazestormer.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import lejos.robotics.pathfinding.Path;
import lejos.util.Delay;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.BarcodeRunnerListener;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.TileType;
import mazestormer.robot.Navigator;
import mazestormer.robot.PathRunner;
import mazestormer.robot.Robot;
import mazestormer.robot.RunnerListener;
import mazestormer.robot.RunnerTask;
import mazestormer.util.CancellationException;

import com.google.common.primitives.Floats;

public class ExplorerRunner extends PathRunner implements NavigationListener {

	private Deque<Tile> queue = new ArrayDeque<Tile>();
	private Tile currentTile;
	private Tile nextTile;

	private final LineFinderRunner lineFinder;
	private final BarcodeRunner barcodeRunner;

	private static final int findLineInterval = 10;
	private int nextFindLine = 1;
	private boolean shouldFindLine = false;

	private State state = State.NEXT;
	private AtomicBoolean enableNavigator = new AtomicBoolean(true);

	public enum State {
		SCAN, ROTATE, TRAVEL, LINE, BARCODE, NEXT;
	}

	public ExplorerRunner(Robot robot, Maze maze) {
		super(robot, maze);

		// Navigator
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
		// TODO Make configurable
		barcodeRunner.setScanSpeed(10);
		barcodeRunner.addBarcodeListener(new BarcodeListener());
	}

	private State getState() {
		return state;
	}

	private void setState(State newState) {
		state = newState;
	}

	private boolean isNavigatorEnabled() {
		return enableNavigator.get();
	}

	private void enableNavigator() {
		while (!enableNavigator.compareAndSet(false, true))
			Thread.yield();
	}

	private void disableNavigator() {
		while (!enableNavigator.compareAndSet(true, false))
			Thread.yield();
	}

	protected void log(String message) {
		System.out.println(message);
	}

	@Override
	public void run() {
		init();
		cycle();
	}

	private void reset() {
		// Stop
		getPilot().stop();
	}

	@Override
	public void onCompleted() {
		super.onCompleted();
		reset();
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		reset();
	}

	@Override
	public void shutdown() {
		// Shutdown everything
		getPilot().stop();
		super.shutdown();
		lineFinder.shutdown();
		barcodeRunner.shutdown();
	}

	private void init() {
		// Queue starts with path only containing the root
		Tile startTile = getCurrentTile();
		queue.clear();
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

		// Scan and update current tile
		setState(State.SCAN);
		if (!currentTile.isExplored()) {
			log("Scan for edges at " + currentTile.getPosition());
			scanAndUpdate(currentTile);
			currentTile.setExplored();
		}
		throwWhenCancelled();

		// Explore
		explore();
	}

	private void explore() {
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
		log("Go to " + goal.getPosition());

		// Create path
		navigator.clearPath();
		List<Waypoint> waypoints = findPath(goal);
		for (Waypoint waypoint : waypoints) {
			navigator.addWaypoint(waypoint);
		}

		// Follow path until before traveling
		setState(State.ROTATE);
		navigator.followPathUntil(Navigator.State.TRAVEL);
	}

	private void beforeTravel() {
		// Start line finder if needed
		if (shouldFindLine) {
			shouldFindLine = false;
			lineFinder.start();
		} else {
			afterLineFinder();
		}
	}

	private void travel() {
		// Update state
		setState(State.TRAVEL);
		// Follow path until way point is reached
		navigator.resumeFrom(Navigator.State.TRAVEL);
	}

	private void afterTravel() {
		// Next step
		next();
	}

	private void next() {
		// Update state
		setState(State.NEXT);
		// Cycle again
		cycle();
	}

	private void beforeLineFinder() {
		// Update state
		setState(State.LINE);
	}

	private void afterLineFinder() {
		// Cancel line finder if still running
		lineFinder.cancel();
		// Start barcode runner
		toggleBarcodeRunner();
		// Travel to way point
		travel();
	}

	private void beforeBarcode() {
		// Stop listening to navigator
		disableNavigator();
		// Update state
		setState(State.BARCODE);
		log("Barcode found, pausing navigation");
		// Cancel line finder if still running
		lineFinder.cancel();
	}

	private void afterBarcode(byte barcode) {
		log("Barcode read, placing on: " + nextTile.getPosition());
		// Set barcode on tile
		setBarcodeTile(nextTile, barcode);
		// Resume listening to navigator
		enableNavigator();
		// Next step
		next();
	}

	private void setBarcodeTile(Tile tile, byte barcode) {
		float relativeHeading = maze.toRelative(getPose().getHeading());
		Orientation heading = angleToOrientation(relativeHeading);

		// Make straight tile
		for (Orientation wall : TileType.STRAIGHT.getWalls(heading)) {
			maze.setEdge(tile.getPosition(), wall, EdgeType.WALL);
		}
		// Replace unknown edges with openings
		for (Orientation orientation : tile.getUnknownSides()) {
			maze.setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
		}
		// Mark as explored
		tile.setExplored();
		// Set barcode
		maze.setBarcode(tile.getPosition(), barcode);
	}

	/**
	 * Scans in the direction of *unknown* edges, and updates them accordingly.
	 */
	private void scanAndUpdate(Tile tile) {
		// Read from scanner
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
		for (Orientation orientation : tile.getUnknownSides()) {
			maze.setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
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
	 * Increment the way point counter which controls when the robot adjust its
	 * position with the line finder. Triggered when a way point is reached.
	 */
	private void incrementFindLineCounter() {
		// Increment counter for line finder
		nextFindLine++;
		if (nextFindLine == findLineInterval) {
			shouldFindLine = true;
			nextFindLine = 0;
		}
	}

	/**
	 * Enables the barcode runner when the next way point is not yet explored.
	 * Triggered when a way point is reached.
	 */
	private void toggleBarcodeRunner() {
		Path path = navigator.getPath();
		// Only scan if next way point is not yet explored
		if (!path.isEmpty() && getTileAt(path.get(0)).isExplored()) {
			barcodeRunner.cancel();
		} else {
			barcodeRunner.start();
		}
	}

	/**
	 * Get the orientation corresponding to the given angle.
	 * 
	 * @param angle
	 *            The angle.
	 */
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

	/**
	 * Normalize the given angle between -180° and +180°.
	 * 
	 * @param angle
	 *            The angle to normalize.
	 */
	private float normalize(float angle) {
		while (angle > 180)
			angle -= 360f;
		while (angle < -180)
			angle += 360f;
		return angle;
	}

	@Override
	public void atWaypoint(Waypoint waypoint, Pose pose, int sequence) {
		incrementFindLineCounter();
		toggleBarcodeRunner();
	}

	private void pathComplete() {
		// Dirty fix for synchronization with barcode
		Delay.msDelay(10);

		if (!isNavigatorEnabled())
			return;

		// Next tile reached
		afterTravel();
	}

	private void pathInterrupted() {
		if (!isNavigatorEnabled())
			return;
		// Paused after rotating and before traveling
		if (getState() == State.ROTATE) {
			beforeTravel();
		}
	}

	@Override
	public void pathComplete(final Waypoint waypoint, Pose pose, int sequence) {
		start(new RunnerTask() {
			@Override
			public void run() throws CancellationException {
				pathComplete();
			}
		});
	}

	@Override
	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
		start(new RunnerTask() {
			@Override
			public void run() throws CancellationException {
				pathInterrupted();
			}
		});
	}

	private class LineFinderListener implements RunnerListener {
		@Override
		public void onStarted() {
			start(new RunnerTask() {
				@Override
				public void run() throws CancellationException {
					beforeLineFinder();
				}
			});
		}

		@Override
		public void onCompleted() {
			start(new RunnerTask() {
				@Override
				public void run() throws CancellationException {
					afterLineFinder();
				}
			});
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
		public void onEndBarcode(final byte barcode) {
			start(new RunnerTask() {
				@Override
				public void run() throws CancellationException {
					afterBarcode(barcode);
				}
			});
		}
	}

}