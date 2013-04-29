package mazestormer.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.barcode.Barcode;
import mazestormer.barcode.BarcodeScanner;
import mazestormer.barcode.BarcodeScannerListener;
import mazestormer.line.LineAdjuster;
import mazestormer.line.LineFinder;
import mazestormer.maze.Edge.EdgeType;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.maze.TileShape;
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
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

import com.google.common.primitives.Floats;

/**
 * Drives the robot in an unknown maze.
 */
public class Driver extends StateMachine<Driver, Driver.ExplorerState> implements StateListener<Driver.ExplorerState>,
		NavigatorListener {

	/*
	 * Settings
	 */
	private final Player player;
	private final PathFinder pathFinder;
	private Commander commander;

	/*
	 * Subroutines
	 */
	private final Navigator navigator;
	private final LineFinder lineFinder;
	private final LineAdjuster lineAdjuster;
	private final BarcodeScanner barcodeScanner;

	/*
	 * Navigation
	 */
	private Tile currentTile;
	private Tile nextTile;

	/**
	 * Flag indicating if the driver should periodically adjust the robot's
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
	 * Tile counter for line finder adjustment.
	 */
	private AtomicInteger lineAdjustCounter = new AtomicInteger(0);
	/**
	 * Flag indicating if the line finder should be run.
	 */
	private AtomicBoolean shouldLineAdjust = new AtomicBoolean(false);
	/**
	 * Flag indicating whether the barcode at the current tile should be
	 * ignored.
	 */
	private AtomicBoolean skipCurrentBarcode = new AtomicBoolean(false);

	public Driver(Player player, Commander commander) {
		this.player = checkNotNull(player);
		this.pathFinder = new PathFinder(getMaze());
		this.commander = checkNotNull(commander);
		addStateListener(this);

		// Navigator
		this.navigator = new Navigator(getRobot().getPilot(), getRobot().getPoseProvider());
		navigator.addNavigatorListener(this);
		navigator.pauseAt(Navigator.NavigatorState.TRAVEL);

		// Line finder
		this.lineFinder = new LineFinder(player);
		this.lineAdjuster = new LineAdjuster(player);
		this.lineAdjuster.bind(lineFinder);
		this.lineFinder.addStateListener(new LineFinderListener());

		// Barcode scanner
		this.barcodeScanner = new BarcodeScanner(player);
		barcodeScanner.addBarcodeListener(new BarcodeListener());
	}

	protected void log(String message) {
		player.getLogger().log(Level.INFO, message);
	}

	/*
	 * Getters and setters
	 */

	public final Commander getCommander() {
		return commander;
	}

	protected ControlMode getMode() {
		return getCommander().getMode();
	}

	protected final PathFinder getPathFinder() {
		return pathFinder;
	}

	public final ControllableRobot getRobot() {
		return (ControllableRobot) player.getRobot();
	}

	public final IMaze getMaze() {
		return player.getMaze();
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

	public boolean isBarcodeActionEnabled() {
		return getMode().isBarcodeActionEnabled();
	}

	private void reset() {
		// Reset state
		getMaze().clear();
	}

	private void stopSubroutines() {
		// Stop subroutines
		navigator.stop();
		lineFinder.stop();
		barcodeScanner.stop();
	}

	/*
	 * States
	 */

	protected void init() {
		// Increment counter for start tile
		incrementLineAdjustCounter();

		// Start cycling
		transition(ExplorerState.NEXT_CYCLE);
	}

	protected void nextCycle() {
		// Get the current tile
		currentTile = getCurrentTile();

		if (!currentTile.isExplored()) {
			// Scan for walls
			transition(ExplorerState.SCAN);
		} else {
			// Go to next tile
			transition(ExplorerState.GO_NEXT_TILE);
		}
	}

	protected void scan() {
		// Scan and update current tile
		log("Scan for edges at (" + currentTile.getX() + ", " + currentTile.getY() + ")");
		bindTransition(scanAndUpdate(currentTile), ExplorerState.AFTER_SCAN);
	}

	protected void afterScan() {
		// Set as explored
		getMaze().setExplored(currentTile.getPosition());

		// Go to next tile
		transition(ExplorerState.GO_NEXT_TILE);
	}

	protected void goToNext() {
		skipToNextTile(false);
	}

	public void skipToNextTile(boolean skipCurrentBarcode) {
		// Get the next tile
		nextTile = getCommander().nextTile(currentTile);

		// Objective completed
		if (nextTile == null) {
			noNextTile();
			return;
		}

		// Create and follow path to next tile
		log("Go to tile (" + nextTile.getX() + ", " + nextTile.getY() + ")");
		List<Tile> tilePath = getMode().createPath(getCurrentTile(), nextTile);
		followPath(tilePath, skipCurrentBarcode);
	}

	/**
	 * Follow the given path.
	 * 
	 * @param tilePath
	 *            The path to follow.
	 * @param skipCurrentBarcode
	 *            True if the barcode at the current tile should be ignored.
	 *            Useful when calling from a barcode action.
	 */
	public void followPath(List<Tile> tilePath, boolean skipCurrentBarcode) {
		// Set path
		navigator.stop();
		navigator.setPath(getPathFinder().toWaypointPath(tilePath));

		// Skip current barcode if requested
		this.skipCurrentBarcode.set(skipCurrentBarcode);

		// Follow path until before traveling
		transition(ExplorerState.NEXT_WAYPOINT);
	}

	/**
	 * @see #followPath(List, boolean)
	 */
	public void followPath(List<Tile> tilePath) {
		followPath(tilePath, false);
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
		/*
		 * Check if barcode action should be executed.
		 * 
		 * Implementation note: order is important! The flag should be cleared
		 * regardless of whether barcode actions are enabled.
		 */
		if (!skipCurrentBarcode.getAndSet(false) && isBarcodeActionEnabled() && currentTile.hasBarcode()) {
			log("Performing barcode action for (" + currentTile.getX() + ", " + currentTile.getY() + ")");
			// Pause navigator
			// Note: Cannot interrupt report state
			if (navigator.getState() != NavigatorState.REPORT) {
				navigator.pause();
			}
			// Resume when action is done
			Future<?> future = getCommander().getAction(currentTile.getBarcode()).performAction(player);
			bindTransition(future, ExplorerState.NAVIGATE);
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
			log("Travel off barcode: " + String.format("%.02f", clearDistance));
			bindTransition(getRobot().getPilot().travelComplete(clearDistance), ExplorerState.BEFORE_TRAVEL);
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

	protected void afterBarcode(Barcode barcode) {
		log("Barcode read, placing on: (" + nextTile.getX() + ", " + nextTile.getY() + ")");
		// Set barcode on tile
		setBarcodeTile(nextTile, barcode);
		// Travel
		transition(ExplorerState.TRAVEL);
	}

	/**
	 * Check if the barcode scanner should be started for the given way point.
	 */
	private boolean shouldBarcode(Waypoint nextWaypoint) {
		if (nextWaypoint == null) {
			// No next way point, assume not explored
			return true;
		} else {
			// Only scan if next way point is not yet explored
			Tile nextTile = getPathFinder().getTileAt(nextWaypoint);
			return !nextTile.isExplored();
		}
	}

	/*
	 * Exploration finished
	 */
	private void noNextTile() {
		// Clean up
		stopSubroutines();
		// Done
		finish();
	}

	/**
	 * Set the barcode and edges of a tile.
	 * 
	 * @param tile
	 *            The tile.
	 * @param barcode
	 *            The barcode.
	 */
	private void setBarcodeTile(Tile tile, Barcode barcode) {
		float relativeHeading = getMaze().toRelative(getPose().getHeading());
		Orientation heading = angleToOrientation(relativeHeading);

		// Make straight tile
		getMaze().setTileShape(tile.getPosition(), new TileShape(TileType.STRAIGHT, heading));
		// Set barcode
		getMaze().setBarcode(tile.getPosition(), barcode);
		// Mark as explored
		getMaze().setExplored(tile.getPosition());
	}

	/**
	 * Scan in the direction of *unknown* edges, and updates them accordingly.
	 * 
	 * @param tile
	 *            The tile to update.
	 */
	private Future<?> scanAndUpdate(final Tile tile) {
		// Read from scanner
		final Future<RangeFeature> future = getRobot().getRangeDetector().scanAsync(getScanAngles(tile));
		// Process when received
		future.addFutureListener(new FutureListener<RangeFeature>() {
			@Override
			public void futureResolved(Future<? extends RangeFeature> future, RangeFeature feature) {
				updateTileEdges(tile, feature);
			}

			@Override
			public void futureCancelled(Future<? extends RangeFeature> future) {
				// Ignore
			}
		});
		return future;
	}

	/**
	 * Update the edges of a tile using the given detected features.
	 * 
	 * @param tile
	 *            The tile to update.
	 * @param feature
	 *            The detected features.
	 */
	private void updateTileEdges(Tile tile, RangeFeature feature) {
		// Place walls
		if (feature != null) {
			float relativeHeading = getMaze().toRelative(feature.getPose().getHeading());
			for (RangeReading reading : feature.getRangeReadings()) {
				Orientation orientation = angleToOrientation(reading.getAngle() + relativeHeading);
				getMaze().setEdge(tile.getPosition(), orientation, EdgeType.WALL);
			}
		}
		// Replace unknown edges with openings
		for (Orientation orientation : tile.getUnknownSides()) {
			getMaze().setEdge(tile.getPosition(), orientation, EdgeType.OPEN);
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
			// Get absolute angle relative to positive X (east) direction
			float angle = getMaze().toAbsolute(Orientation.EAST.angleTo(direction));
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
		Waypoint currentWaypoint = getPathFinder().toWaypoint(getCurrentTile());
		Waypoint nextWaypoint = navigator.getCurrentTarget();

		// Get traveling line
		Line line = new Line(currentWaypoint.x, currentWaypoint.y, nextWaypoint.x, nextWaypoint.y);
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
	 * Get the tile at which the robot is currently located.
	 */
	public Tile getCurrentTile() {
		return getPathFinder().getTileAt(getPose());
	}

	/**
	 * Get the next tile to which the robot is navigating.
	 */
	public Tile getNextTile() {
		return nextTile;
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
		stopSubroutines();
		// Initialize
		transition(ExplorerState.INIT);
	}

	@Override
	public void stateStopped() {
		log("Exploration stopped.");
		// Stop
		stopSubroutines();
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
	public void navigatorPaused(NavigatorState currentState, Pose pose, boolean onTransition) {
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

	private class LineFinderListener extends AbstractStateListener<LineFinder.LineFinderState> {
		@Override
		public void stateFinished() {
			transition(ExplorerState.AFTER_LINE_ADJUST);
		}
	}

	private class BarcodeListener implements BarcodeScannerListener {
		@Override
		public void onStartBarcode() {
			transition(ExplorerState.BEFORE_BARCODE);
		}

		@Override
		public void onEndBarcode(final Barcode barcode) {
			afterBarcode(barcode);
		}
	}

	public enum ExplorerState implements State<Driver, ExplorerState> {
		INIT {
			@Override
			public void execute(Driver explorer) {
				explorer.init();
			}
		},
		NEXT_CYCLE {
			@Override
			public void execute(Driver explorer) {
				explorer.nextCycle();
			}
		},
		SCAN {
			@Override
			public void execute(Driver explorer) {
				explorer.scan();
			}
		},
		AFTER_SCAN {
			@Override
			public void execute(Driver explorer) {
				explorer.afterScan();
			}
		},
		GO_NEXT_TILE {
			@Override
			public void execute(Driver explorer) {
				explorer.goToNext();
			}
		},
		NEXT_WAYPOINT {
			@Override
			public void execute(Driver explorer) {
				explorer.nextWaypoint();
			}
		},
		BARCODE_ACTION {
			@Override
			public void execute(Driver explorer) {
				explorer.barcodeAction();
			}
		},
		NAVIGATE {
			@Override
			public void execute(Driver explorer) {
				explorer.navigate();
			}
		},
		CLEAR_BARCODE {
			@Override
			public void execute(Driver explorer) {
				explorer.clearBarcode();
			}
		},
		BEFORE_TRAVEL {
			@Override
			public void execute(Driver explorer) {
				explorer.beforeTravel();
			}
		},
		AFTER_LINE_ADJUST {
			@Override
			public void execute(Driver explorer) {
				explorer.afterLineAdjust();
			}
		},
		BEFORE_BARCODE {
			@Override
			public void execute(Driver explorer) {
				explorer.beforeBarcode();
			}
		},
		TRAVEL {
			@Override
			public void execute(Driver explorer) {
				explorer.travel();
			}
		}
	}

}