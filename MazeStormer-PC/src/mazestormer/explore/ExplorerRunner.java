package mazestormer.explore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.barcode.BarcodeMapping;
import mazestormer.barcode.BarcodeRunner;
import mazestormer.barcode.BarcodeRunnerListener;
import mazestormer.barcode.BarcodeSpeed;
import mazestormer.line.LineFinderRunner;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.Maze;
import mazestormer.maze.Maze.Target;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.maze.TileType;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Navigator;
import mazestormer.robot.Navigator.NavigatorState;
import mazestormer.robot.NavigatorListener;
import mazestormer.state.AbstractStateListener;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;

import com.google.common.primitives.Floats;

public class ExplorerRunner extends
		StateMachine<ExplorerRunner, ExplorerRunner.ExplorerState> implements
		StateListener<ExplorerRunner.ExplorerState>, NavigatorListener {

	/*
	 * Subroutines
	 */
	private final Navigator navigator;
	private final LineFinderRunner lineFinder;
	private final BarcodeRunner barcodeScanner;

	/*
	 * Exploration
	 */
	private final LinkedList<Tile> queue = new LinkedList<Tile>();
	private final PathFinder pathFinder;
	private Tile currentTile;
	private Tile nextTile;

	/*
	 * Settings
	 */
	private final Player player;

	/**
	 * Flag indicating if the explorer should periodically adjust the robot's
	 * position by running the line finder.
	 */
	private boolean lineAdjustEnabled = true;
	/**
	 * The amount of tiles between two line finder adjustment runs.
	 */
	private int lineAdjustInterval = 10;

	/*
	 * State
	 */

	/**
	 * Flag indicating if the maze is fully explored.
	 */
	private AtomicBoolean isExplored = new AtomicBoolean(false);
	/**
	 * Tile counter for line finder adjustment.
	 */
	private AtomicInteger lineAdjustCounter = new AtomicInteger(0);
	/**
	 * Flag indicating if the line finder should be run.
	 */
	private AtomicBoolean shouldLineAdjust = new AtomicBoolean(false);

	public ExplorerRunner(Player player) {
		this.player = checkNotNull(player);
		addStateListener(this);

		// Navigator
		this.navigator = new Navigator(getRobot().getPilot(), getRobot()
				.getPoseProvider());
		navigator.addNavigatorListener(this);
		navigator.pauseAt(Navigator.NavigatorState.TRAVEL);

		// Path finder
		this.pathFinder = new PathFinder(getMaze());

		// Line finder
		this.lineFinder = new LineFinderRunner(getRobot()) {
			@Override
			protected void log(String message) {
				ExplorerRunner.this.log(message);
			}
		};
		lineFinder.addStateListener(new LineFinderListener());

		// Barcode scanner
		this.barcodeScanner = new BarcodeRunner(player) {
			@Override
			protected void log(String message) {
				ExplorerRunner.this.log(message);
			}
		};
		barcodeScanner.addBarcodeListener(new BarcodeListener());

		// Barcode actions are manually executed
		barcodeScanner.setPerformAction(false);
	}

	public ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	public Maze getMaze() {
		return player.getMaze();
	}

	protected void log(String message) {
		System.out.println(message);
	}

	/*
	 * Getters and setters
	 */

	public boolean isExplored() {
		return isExplored.get();
	}

	public void setExplored(boolean isExplored) {
		this.isExplored.set(isExplored);
	}

	public boolean isLineAdjustEnabled() {
		return lineAdjustEnabled;
	}

	public void setLineAdjustEnabled(boolean isEnabled) {
		this.lineAdjustEnabled = isEnabled;
	}

	public int getLineAdjustInterval() {
		return lineAdjustInterval;
	}

	public void setLineAdjustInterval(int interval) {
		this.lineAdjustInterval = interval;
	}

	public void setScanSpeed(double scanSpeed) {
		barcodeScanner.setScanSpeed(scanSpeed);
	}

	public void setBarcodeMapping(BarcodeMapping mapping) {
		barcodeScanner.setMapping(mapping);
	}

	private void reset() {
		// Reset state
		getMaze().clear();
		setExplored(false);
		// Stop subroutines
		navigator.stop();
		lineFinder.stop();
		barcodeScanner.stop();
	}

	/*
	 * States
	 */

	protected void init() {
		// Queue starts with path only containing the start tile
		Tile startTile = pathFinder.getTileAt(getPose());
		queue.clear();
		queue.addFirst(startTile);

		// Increment counter for start tile
		incrementLineAdjustCounter();

		// Start cycling
		transition(ExplorerState.NEXT_CYCLE);
	}

	protected void nextCycle() {
		if (queue.isEmpty() || !isRunning()) {
			endOfQueue();
			return;
		}

		// Remove the first path from the queue
		// This is the current tile of the robot
		currentTile = queue.pollFirst();

		// Scan and update current tile
		if (!currentTile.isExplored()) {
			log("Scan for edges at " + currentTile.getPosition());
			scanAndUpdate(currentTile);
			currentTile.setExplored();
		}

		// Create new paths to all neighbors
		selectTiles(currentTile);

		// Destination reached
		if (queue.isEmpty()) {
			endOfQueue();
			return;
		}

		// Sort queue if exploring
		if (!isExplored()) {
			Collections.sort(queue, new ClosestTileComparator(currentTile));
		}

		// Go to next tile
		nextTile = queue.peekFirst();
		log("Go to " + nextTile.getPosition());

		// Create path
		navigator.stop();
		navigator.setPath(pathFinder.findPath(getCurrentTile(), nextTile));

		// Follow path until before traveling
		transition(ExplorerState.NEXT_WAYPOINT);
	}

	protected void nextWaypoint() {
		// Clean up
		barcodeScanner.stop();

		// Update flags
		incrementLineAdjustCounter();

		// Execute barcode action on current way point
		transition(ExplorerState.BARCODE_ACTION);
	}

	protected void barcodeAction() {
		Tile currentTile = getCurrentTile();
		// Skip barcodes when traveling to checkpoint or goal
		if (!isExplored() && currentTile.hasBarcode()) {
			log("Performing barcode action for " + currentTile.getPosition());
			// Resume when action is done
			bindTransition(
					barcodeScanner.performAction(currentTile.getBarcode()),
					ExplorerState.NAVIGATE);
		} else {
			// No action, just continue
			transition(ExplorerState.NAVIGATE);
		}
	}

	protected void navigate() {
		// Navigate until about to travel
		navigator.pauseAt(NavigatorState.TRAVEL);

		// Start navigation
		if (navigator.isRunning()) {
			navigator.resume();
		} else {
			navigator.start();
		}
	}

	protected void clearBarcode() {
		if (getCurrentTile().hasBarcode()) {
			// Travel off barcode
			double clearDistance = getBarcodeClearingDistance();
			log("Travel off barcode: " + clearDistance);
			bindTransition(getRobot().getPilot().travelComplete(clearDistance),
					ExplorerState.BEFORE_TRAVEL);
		} else {
			// No barcode
			transition(ExplorerState.BEFORE_TRAVEL);
		}
	}

	protected void beforeTravel() {
		if (isLineAdjustEnabled() && shouldLineAdjust()) {
			// Start line finder
			lineFinder.start();
		} else {
			// No need to line adjust
			transition(ExplorerState.AFTER_LINE_ADJUST);
		}
	}

	protected void afterLineAdjust() {
		// Stop line finder, if still running
		lineFinder.stop();

		// Start barcode scanner if necessary
		if (shouldBarcode(navigator.getCurrentTarget())) {
			barcodeScanner.start();
		}

		// Travel
		transition(ExplorerState.TRAVEL);
	}

	private void travel() {
		// Navigate until way point reached
		navigator.pauseAt(NavigatorState.NEXT);
		navigator.resume();
	}

	protected void beforeBarcode() {
		// Pause navigator immediately
		navigator.pause();

		log("Barcode found, pausing navigation");
	}

	protected void afterBarcode(byte barcode) {
		log("Barcode read, placing on: " + nextTile.getPosition());
		// Set barcode on tile
		setBarcodeTile(nextTile, barcode);
		// Travel
		transition(ExplorerState.TRAVEL);
	}

	/**
	 * Check if the barcode scanner should be started for the given way point.
	 */
	private boolean shouldBarcode(Waypoint nextWaypoint) {
		if (isExplored()) {
			// Everything is already explored
			return false;
		} else if (nextWaypoint == null) {
			// No next way point, assume not explored
			return true;
		} else {
			// Only scan if next way point is not yet explored
			Tile nextTile = pathFinder.getTileAt(nextWaypoint);
			return !nextTile.isExplored();
		}
	}

	/*
	 * Exploration finished
	 */
	private void endOfQueue() {
		log("End of queue reached");
		if (isExplored()) {
			// Done
			finish();
		} else {
			// Go to finish
			goToFinish();
		}
	}

	private void goToFinish() {
		log("Traveling to checkpoint and goal");
		// Set as explored
		setExplored(true);

		// Add goal
		Tile goal = getMaze().getTarget(Target.GOAL);
		if (goal != null) {
			queue.addFirst(goal);
		}
		// Add checkpoint before goal
		Tile checkPoint = getMaze().getTarget(Target.CHECKPOINT);
		if (checkPoint != null) {
			queue.addFirst(checkPoint);
		}
		// Add current tile
		queue.addFirst(getCurrentTile());

		// Clean up to prevent barcode scanner from resetting the new speed
		barcodeScanner.stop();
		// Start traveling at high speed
		getRobot().getPilot().setTravelSpeed(
				BarcodeSpeed.HIGH.getBarcodeSpeedValue());

		// Traverse new path
		transition(ExplorerState.NEXT_CYCLE);
	}

	/**
	 * Set the barcode and edges of a tile.
	 * 
	 * @param tile
	 *            The tile.
	 * @param barcode
	 *            The barcode.
	 */
	private void setBarcodeTile(Tile tile, byte barcode) {
		float relativeHeading = getMaze().toRelative(getPose().getHeading());
		Orientation heading = angleToOrientation(relativeHeading);

		// Make straight tile
		for (Orientation wall : TileType.STRAIGHT.getWalls(heading)) {
			getMaze().setEdge(tile.getPosition(), wall, EdgeType.WALL);
		}
		// Replace unknown edges with openings
		for (Orientation orientation : tile.getUnknownSides()) {
			getMaze().setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
		}
		// Mark as explored
		tile.setExplored();
		// Set barcode
		getMaze().setBarcode(tile.getPosition(), barcode);
	}

	/**
	 * Scan in the direction of *unknown* edges, and updates them accordingly.
	 */
	private void scanAndUpdate(Tile tile) {
		// Read from scanner
		RangeFeature feature = getRobot().getRangeDetector().scan(
				getScanAngles(tile));
		// Place walls
		if (feature != null) {
			Orientation orientation;
			for (RangeReading reading : feature.getRangeReadings()) {
				orientation = angleToOrientation(reading.getAngle()
						+ getMaze().toRelative(getPose().getHeading()));
				getMaze().setEdge(tile.getPosition(), orientation,
						EdgeType.WALL);
			}
		}
		// Replace unknown edges with openings
		for (Orientation orientation : tile.getUnknownSides()) {
			getMaze().setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
		}
	}

	/**
	 * Add tiles to the queue if the edge in its direction is open and it is not
	 * explored yet.
	 */
	private void selectTiles(Tile tile) {
		for (Orientation direction : tile.getOpenSides()) {
			Tile neighborTile = getMaze().getOrCreateNeighbor(tile, direction);
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
	 * Get the distance necessary to travel off the barcode on which the robot
	 * is currently located.
	 */
	private double getBarcodeClearingDistance() {
		Point currentPosition = getPose().getLocation();
		Waypoint currentWaypoint = pathFinder.toWaypoint(getCurrentTile());
		Waypoint nextWaypoint = navigator.getCurrentTarget();

		// Get traveling line
		Line line = new Line(currentWaypoint.x, currentWaypoint.y,
				nextWaypoint.x, nextWaypoint.y);
		float angle = currentWaypoint.angleTo(nextWaypoint);

		// Get target position to clear barcode
		Point target = currentWaypoint.pointAt(getBarcodeClearing(), angle);

		// Project position on traveling line to ignore offsets from center line
		Point clearing = currentPosition.projectOn(line);

		if (nextWaypoint.distance(clearing) >= nextWaypoint.distance(target)) {
			// Robot is further away from way point than clearing target
			return target.distance(clearing);
		} else {
			// Robot is closer to way point than clearing target
			return 0d;
		}
	}

	/**
	 * Get the distance necessary to travel off a barcode.
	 */
	private float getBarcodeClearing() {
		return getMaze().getTileSize() / 3f;
	}

	/*
	 * Line adjust counter
	 */

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
	private void incrementLineAdjustCounter() {
		// Increment counter for line finder
		if (lineAdjustCounter.incrementAndGet() >= getLineAdjustInterval()) {
			shouldLineAdjust.set(true);
			lineAdjustCounter.set(0);
		}
	}

	/*
	 * Helpers
	 */

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

	/**
	 * Get the current tile at which the robot is located.
	 */
	public Tile getCurrentTile() {
		return pathFinder.getTileAt(getPose());
	}

	/**
	 * Get the next tile to which the robot is navigating.
	 */
	public Tile getNextTile() {
		return nextTile;
	}

	/**
	 * Pop the next tile from the queue. Internal use only.
	 */
	public Tile pollTile() {
		return queue.pollFirst();
	}

	/**
	 * Restart the cycle for the current tile. Internal use only.
	 */
	public void restartCycle() {
		// Re-add current tile to queue
		queue.addFirst(currentTile);
		// Next cycle
		transition(ExplorerState.NEXT_CYCLE);
	}

	/**
	 * Get the current pose of the robot.
	 */
	private Pose getPose() {
		return getRobot().getPoseProvider().getPose();
	}

	/*
	 * State
	 */

	@Override
	public void stateStarted() {
		log("Exploration started.");
		// Reset
		reset();
		// Initialize
		transition(ExplorerState.INIT);
	}

	@Override
	public void stateStopped() {
		log("Exploration stopped.");
	}

	@Override
	public void stateFinished() {
		log("Exploration completed.");
	}

	@Override
	public void statePaused(ExplorerState currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(ExplorerState currentState) {
	}

	@Override
	public void stateTransitioned(ExplorerState nextState) {
	}

	/*
	 * Navigator
	 */

	@Override
	public void navigatorStarted(Pose pose) {
	}

	@Override
	public void navigatorStopped(Pose pose) {
	}

	@Override
	public void navigatorPaused(NavigatorState currentState, Pose pose,
			boolean onTransition) {
		// Only respond to pauses on transitions
		if (!onTransition)
			return;

		if (getState() == ExplorerState.NAVIGATE) {
			// Paused after rotating and before traveling
			assert (currentState == NavigatorState.TRAVEL);
			transition(ExplorerState.CLEAR_BARCODE);
		}
	}

	@Override
	public void navigatorResumed(NavigatorState currentState, Pose pose) {
	}

	@Override
	public void navigatorAtWaypoint(Waypoint waypoint, Pose pose) {
		// At way point
		if (!navigator.pathCompleted()) {
			transition(ExplorerState.NEXT_WAYPOINT);
		}
	}

	@Override
	public void navigatorCompleted(Waypoint waypoint, Pose pose) {
		// Path completed
		transition(ExplorerState.NEXT_CYCLE);
	}

	private class LineFinderListener extends
			AbstractStateListener<LineFinderRunner.LineFinderState> {
		@Override
		public void stateFinished() {
			transition(ExplorerState.AFTER_LINE_ADJUST);
		}
	}

	private class BarcodeListener implements BarcodeRunnerListener {
		@Override
		public void onStartBarcode() {
			transition(ExplorerState.BEFORE_BARCODE);
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
			double leftDistance = manhattanDistance(referencePosition,
					left.getPosition());
			double rightDistance = manhattanDistance(referencePosition,
					right.getPosition());
			return Double.compare(leftDistance, rightDistance);
		}

		public static double manhattanDistance(Point2D left, Point2D right) {
			return Math.abs(left.getX() - right.getX())
					+ Math.abs(left.getY() - right.getY());
		}

	}

	public enum ExplorerState implements State<ExplorerRunner, ExplorerState> {
		INIT {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.init();
			}
		},
		NEXT_CYCLE {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.nextCycle();
			}
		},
		NEXT_WAYPOINT {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.nextWaypoint();
			}
		},
		BARCODE_ACTION {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.barcodeAction();
			}
		},
		NAVIGATE {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.navigate();
			}
		},
		CLEAR_BARCODE {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.clearBarcode();
			}
		},
		BEFORE_TRAVEL {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.beforeTravel();
			}
		},
		AFTER_LINE_ADJUST {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.afterLineAdjust();
			}
		},
		BEFORE_BARCODE {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.beforeBarcode();
			}
		},
		TRAVEL {
			@Override
			public void execute(ExplorerRunner explorer) {
				explorer.travel();
			}
		}
	}

}