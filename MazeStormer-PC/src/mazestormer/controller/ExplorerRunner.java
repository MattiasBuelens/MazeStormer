package mazestormer.controller;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.BarcodeRunnerListener;
import mazestormer.barcode.BarcodeSpeed;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Maze.Target;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.TileType;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Navigator2;
import mazestormer.robot.NavigatorListener;
import mazestormer.robot.PathRunner;
import mazestormer.robot.RunnerListener;
import mazestormer.robot.RunnerTask;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

import com.google.common.primitives.Floats;

public class ExplorerRunner extends PathRunner implements NavigatorListener {

	private LinkedList<Tile> queue = new LinkedList<Tile>();
	private Tile currentTile;
	private Tile nextTile;

	private final LineFinderRunner lineFinder;
	private final BarcodeRunner barcodeRunner;

	public enum State {
		SCAN, ROTATE, TRAVEL, LINE, BARCODE, NEXT;
	}

	/**
	 * Flag indicating if the maze is fully explored.
	 */
	private boolean isExplored = false;
	/**
	 * Current state of the explorer.
	 */
	private State state = State.NEXT;
	/**
	 * Flag indicating if the barcode scanner should be run.
	 */
	private AtomicBoolean shouldBarcode = new AtomicBoolean(false);
	/**
	 * Flag indicating if the explorer should periodically adjust the robot's
	 * position by running the line finder.
	 */
	private AtomicBoolean lineAdjustEnabled = new AtomicBoolean(true);
	/**
	 * The amount of tiles between two line finder adjustment runs.
	 */
	private int lineAdjustInterval = 10;
	/**
	 * Tile counter for line finder adjustment.
	 */
	private AtomicInteger lineAdjustCounter = new AtomicInteger(0);
	/**
	 * Flag indicating if the line finder should be run.
	 */
	private AtomicBoolean shouldLineAdjust = new AtomicBoolean(false);

	public ExplorerRunner(ControllableRobot robot, Maze maze) {
		super(robot, maze);

		// Navigator
		navigator.addNavigatorListener(this);
		navigator.pauseAt(Navigator2.State.TRAVEL);

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
		// Barcode actions are manually executed
		barcodeRunner.setPerformAction(false);
	}

	protected void log(String message) {
		System.out.println(message);
	}

	public boolean isExplored() {
		return isExplored;
	}

	public void setExplored(boolean isExplored) {
		this.isExplored = isExplored;
	}

	private State getState() {
		return state;
	}

	private void setState(State newState) {
		state = newState;
	}

	public boolean isLineAdjustEnabled() {
		return lineAdjustEnabled.get();
	}

	public void setLineAdjustEnabled(boolean isEnabled) {
		lineAdjustEnabled.set(isEnabled);
	}

	public int getLineAdjustInterval() {
		return lineAdjustInterval;
	}

	public void setLineAdjustInterval(int interval) {
		this.lineAdjustInterval = interval;
	}

	public void setScanSpeed(double scanSpeed) {
		barcodeRunner.setScanSpeed(scanSpeed);
	}

	@Override
	public void run() {
		init();
		cycle();
	}

	private void reset() {
		// Stop
		navigator.stop();
		getPilot().stop();
		// Clear state
		lineAdjustCounter.set(0);
	}

	@Override
	public void onCompleted() {
		log("Explorer completed.");
		super.onCompleted();
		reset();
	}

	@Override
	public void onCancelled() {
		log("Explorer cancelled.");
		super.onCancelled();
		reset();
	}

	@Override
	public void shutdown() {
		// Shutdown everything
		super.shutdown();
		lineFinder.shutdown();
		barcodeRunner.shutdown();
		getPilot().stop();
	}

	private void init() {
		// Reset state
		maze.clear();
		setExplored(false);
		setState(State.NEXT);
		incrementFindLineCounter();
		navigator.stop();

		// Queue starts with path only containing the root
		Tile startTile = getCurrentTile();
		queue.clear();
		queue.addFirst(startTile);
	}

	private void cycle() {
		if (queue.isEmpty() || !isRunning()) {
			onEmptyQueue();
			return;
		}

		// Remove the first path from the queue
		// This is the current tile of the robot
		currentTile = queue.pollFirst();

		// Scan and update current tile
		setState(State.SCAN);
		if (!currentTile.isExplored()) {
			log("Scan for edges at " + currentTile.getPosition());
			scanAndUpdate(currentTile);
			currentTile.setExplored();
		}
		throwWhenCancelled();

		// Create new paths to all neighbors
		selectTiles(currentTile);

		// Destination reached
		if (queue.isEmpty()) {
			onEmptyQueue();
			return;
		}

		// Sort queue if exploring
		if (!isExplored()) {
			Collections.sort(queue, new ClosestTileComparator(currentTile));
		}

		// Go to next tile
		nextTile = queue.peekFirst();
		goTo(nextTile);
	}

	private void onEmptyQueue() {
		log("End of queue reached");
		if (isExplored()) {
			// Done
			resolve();
		} else {
			// Finish
			finish();
		}
	}

	private void finish() {
		log("Traveling to checkpoint and goal");
		// Set as explored
		setExplored(true);
		// Add goal
		Tile goal = maze.getTarget(Target.GOAL);
		if (goal != null) {
			queue.addFirst(goal);
		}
		// Add checkpoint before goal
		Tile checkPoint = maze.getTarget(Target.CHECKPOINT);
		if (checkPoint != null) {
			queue.addFirst(checkPoint);
		}
		// Add current tile
		queue.addFirst(getCurrentTile());
		// Start traveling at high speed
		setTravelSpeed(BarcodeSpeed.HIGH.getBarcodeSpeedValue());
		cycle();
	}

	private void goTo(Tile goal) {
		throwWhenCancelled();
		log("Go to " + goal.getPosition());

		// Create path
		navigator.stop();
		navigator.setPath(findPath(goal));

		// Toggle barcode
		toggleBarcode();

		// Follow path until before traveling
		setState(State.ROTATE);
		navigator.start();
	}

	private void beforeTravel() {
		throwWhenCancelled();
		// Start line finder if needed
		if (isLineAdjustEnabled() && shouldLineAdjust()) {
			lineFinder.start();
		} else {
			afterLineFinder();
		}
	}

	private void travel() {
		throwWhenCancelled();
		// Update state
		setState(State.TRAVEL);
		// Continue path
		navigator.resume();
	}

	private void next() {
		throwWhenCancelled();
		// Update state
		setState(State.NEXT);
		// Cycle again
		cycle();
	}

	private void beforeLineFinder() {
		throwWhenCancelled();
		// Update state
		setState(State.LINE);
	}

	private void afterLineFinder() {
		throwWhenCancelled();
		// Cancel line finder if still running
		lineFinder.cancel();
		// Start barcode runner
		if (shouldBarcode()) {
			barcodeRunner.start();
		}
		// Travel to way point
		travel();
	}

	private boolean shouldBarcode() {
		return shouldBarcode.getAndSet(false);
	}

	private void beforeBarcode() {
		// Pause navigator
		navigator.pause();
		stop();
		// Update state
		setState(State.BARCODE);
		log("Barcode found, pausing navigation");
		// Cancel line finder if still running
		lineFinder.cancel();
	}

	private void afterBarcode(byte barcode) {
		throwWhenCancelled();
		log("Barcode read, placing on: " + nextTile.getPosition());
		// Set barcode on tile
		setBarcodeTile(nextTile, barcode);
		// Execute barcode action
		// executeBarcodeAction();
		// OR just resume
		navigator.resume();
	}

	private void beforeBarcodeAction() {
		// Pause navigator while executing action
		navigator.pause();
	}

	private void afterBarcodeAction(boolean wasBarcode) {
		// Continue navigation
		navigator.resume();
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
	 * Scan in the direction of *unknown* edges, and updates them accordingly.
	 */
	private void scanAndUpdate(Tile tile) {
		// Read from scanner
		RangeFeature feature = robot.getRangeDetector().scan(getScanAngles(tile));
		// Place walls
		if (feature != null) {
			Orientation orientation;
			for (RangeReading reading : feature.getRangeReadings()) {
				orientation = angleToOrientation(reading.getAngle() + maze.toRelative(getPose().getHeading()));
				maze.setEdge(tile.getPosition(), orientation, EdgeType.WALL);
			}
		}
		// Replace unknown edges with openings
		for (Orientation orientation : tile.getUnknownSides()) {
			maze.setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
		}
	}

	/**
	 * Add tiles to the queue if the edge in its direction is open and it is not
	 * explored yet.
	 */
	private void selectTiles(Tile tile) {
		for (Orientation direction : tile.getOpenSides()) {
			Tile neighborTile = maze.getOrCreateNeighbor(tile, direction);
			// Reject the new paths with loops
			if (!neighborTile.isExplored() && !queue.contains(neighborTile)) {
				// Add the new paths to front of queue
				queue.addFirst(neighborTile);
			}
		}
	}

	/**
	 * Get the angles towards <strong>unknown</strong> edges to scan.
	 */
	private float[] getScanAngles(Tile tile) {
		List<Float> list = new ArrayList<Float>();
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
	 * Check whether the robot should adjust its position with the line finder
	 * and resets the flag. Triggered before traveling to a tile.
	 */
	private boolean shouldLineAdjust() {
		return shouldLineAdjust.getAndSet(false);
	}

	/**
	 * Increment the way point counter which controls when the robot adjust its
	 * position with the line finder. Triggered when a way point is reached.
	 */
	private void incrementFindLineCounter() {
		// Increment counter for line finder
		if (lineAdjustCounter.incrementAndGet() >= getLineAdjustInterval()) {
			shouldLineAdjust.set(true);
			lineAdjustCounter.set(0);
		}
	}

	/**
	 * Enable the barcode runner when the next way point is not yet explored.
	 * Triggered when a way point is reached.
	 */
	private void toggleBarcode() {
		if (isExplored()) {
			// Everything is already explored
			shouldBarcode.set(false);
		} else {
			// Only scan if next way point is not yet explored
			Waypoint nextWaypoint = navigator.getCurrentTarget();
			if (nextWaypoint == null) {
				shouldBarcode.set(true);
			} else {
				Tile nextTile = getTileAt(nextWaypoint);
				// if (path.isEmpty() || !getTileAt(path.get(0)).isExplored()) {
				shouldBarcode.set(!nextTile.isExplored());
			}
		}
	}

	/**
	 * Execute the barcode action of the current tile.
	 */
	private void executeBarcodeAction() {
		Tile currentTile = getCurrentTile();
		// Skip barcodes when traveling to checkpoint or goal
		if (!isExplored() && currentTile.hasBarcode()) {
			log("Performing barcode action for " + currentTile.getPosition());
			beforeBarcodeAction();
			// Resume when action is done
			Future<?> future = barcodeRunner.performAction(currentTile.getBarcode());
			future.addFutureListener(new FutureListener<Object>() {
				@Override
				public void futureResolved(Future<?> future) {
					afterBarcodeAction(true);
				}

				@Override
				public void futureCancelled(Future<?> future) {
				}
			});
		} else {
			// No action, just continue
			afterBarcodeAction(false);
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
	public void navigatorStarted(Pose pose) {
	}

	@Override
	public void navigatorStopped(Pose pose) {
	}

	@Override
	public void navigatorAtWaypoint(Waypoint waypoint, Pose pose) {
		// Update flags
		incrementFindLineCounter();
		toggleBarcode();
		// Cancel barcode scanner if still running
		barcodeRunner.cancel();
		// Execute barcode action on this tile
		executeBarcodeAction();
	}

	@Override
	public void navigatorPaused(Pose pose, boolean onTransition) {
		// Only respond to pauses on transitions
		if (!onTransition)
			return;

		switch (navigator.getState()) {
		case TRAVEL:
			// Paused after rotating and before traveling
			beforeTravel();
			break;
		default:
			break;
		}
	}

	@Override
	public void navigatorResumed(Pose pose) {
	}

	@Override
	public void navigatorCompleted(Waypoint waypoint, Pose pose) {
		// Next tile reached
		next();
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
			afterBarcode(barcode);
		}
	}

	/**
	 * Compares tiles based on their Manhattan distance to a given reference
	 * tile.
	 */
	public static class ClosestTileComparator implements Comparator<Tile> {

		private final Point2D referencePosition;

		public ClosestTileComparator(Tile referenceTile) {
			this.referencePosition = referenceTile.getPosition();
		}

		@Override
		public int compare(Tile left, Tile right) {
			double leftDistance = manhattanDistance(referencePosition, left.getPosition());
			double rightDistance = manhattanDistance(referencePosition, right.getPosition());
			return Double.compare(leftDistance, rightDistance);
		}

		public static double manhattanDistance(Point2D left, Point2D right) {
			return Math.abs(left.getX() - right.getX()) + Math.abs(left.getY() - right.getY());
		}

	}

}
