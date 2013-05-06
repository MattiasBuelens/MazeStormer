package mazestormer.command.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mazestormer.command.Driver;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.maze.path.FindCorridorAStar;
import mazestormer.maze.path.MazeAStar;
import mazestormer.robot.ControllablePCRobot;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.WaitFuture;

import com.google.common.base.Predicate;

public class CollisionAvoider extends StateMachine<CollisionAvoider, CollisionAvoider.AvoiderState> implements
		StateListener<CollisionAvoider.AvoiderState> {

	public static final int MINIMUM_TIMEOUT = 2;
	public static final int MAXIMUM_TIMEOUT = 10;

	private final Driver driver;

	public CollisionAvoider(Driver driver) {
		this.driver = driver;
		addStateListener(this);
	}

	protected final Driver getDriver() {
		return driver;
	}

	protected final ControllablePCRobot getRobot() {
		return getDriver().getRobot();
	}

	protected final IMaze getMaze() {
		return getDriver().getMaze();
	}

	protected void log(String message) {
		getDriver().getPlayer().getLogger().warning(message);
	}

	/**
	 * Check if the robot is currently blocked by another robot.
	 */
	public boolean isBlocked() {
		// TODO Check whether reading is within certain range?
		return getRobot().getRobotIRSensor().hasReading();
	}

	/**
	 * Check if currently driving to a corridor.
	 */
	public boolean isDrivingToCorridor() {
		return isRunning() && getState() == AvoiderState.DRIVING;
	}

	/**
	 * Inform that the driver has reached the corridor.
	 */
	public void atCorridor() {
		transition(AvoiderState.WAITING);
	}

	/**
	 * Stare down the other robot.
	 */
	protected void stare() {
		log("Robot detected, avoiding collision");

		// Wait for a random time
		WaitFuture<?> future = new WaitFuture<Object>();
		int timeout = getRandomTimeout();
		log("Awaiting other robot's move for " + timeout + " seconds");

		bindTransition(future, AvoiderState.DRIVE);
		future.resolveAfter(null, timeout * 1000l);
	}

	/**
	 * Try to drive to a corridor if still blocked.
	 */
	protected void driveToCorridor() {
		// Check if other robot moved
		if (!isBlocked()) {
			log("Other robot resolved conflict, continuing");
			transition(AvoiderState.FINISH);
			return;
		}

		// Get current and blocked tile
		Tile currentTile = getDriver().getCurrentTile();
		Tile blockedTile = getDriver().getFacingTile();

		// Find path to corridor
		List<Tile> path = null;
		path = findPathToCorridor(currentTile, blockedTile);
		if (path.isEmpty()) {
			// Cannot move
			log("Unable to move, retrying");
			transition(AvoiderState.FINISH);
		} else {
			// Follow path
			Tile target = path.get(path.size() - 1);
			log("Driving to corridor at (" + target.getX() + ", " + target.getY() + ")");
			getDriver().skipCurrentBarcode(true);
			getDriver().followPath(path);
			transition(AvoiderState.DRIVING);
		}
	}

	/**
	 * Driving to the corridor.
	 */
	protected void drivingToCorridor() {
	}

	/**
	 * Wait in the corridor.
	 */
	protected void waitInCorridor() {
		// Wait for a random time
		WaitFuture<?> future = new WaitFuture<Object>();
		int timeout = getRandomTimeout();
		log("Waiting for " + timeout + " seconds in corridor");

		bindTransition(future, AvoiderState.FINISH);
		future.resolveAfter(null, timeout * 1000l);
	}

	private List<Tile> findPathToCorridor(final Tile startTile, final Tile blockedTile) {
		// Get path of tiles
		MazeAStar astar = new FindCorridorAStar(getMaze(), startTile, new Predicate<Tile>() {
			@Override
			public boolean apply(Tile tile) {
				// Ignore blocked tile
				if (tile.getPosition().equals(blockedTile.getPosition()))
					return false;
				// Ignore seesaws
				if (tile.isSeesaw())
					return false;
				return true;
			}
		});
		List<Tile> tilePath = astar.findPath();
		// Skip starting tile
		if (tilePath == null || tilePath.size() <= 1)
			return new ArrayList<Tile>();
		else
			return tilePath.subList(1, tilePath.size());
	}

	private int getRandomTimeout() {
		return MINIMUM_TIMEOUT + new Random().nextInt(MAXIMUM_TIMEOUT - MINIMUM_TIMEOUT + 1);
	}

	@Override
	public void stateStarted() {
		transition(AvoiderState.STARE);
	}

	@Override
	public void stateStopped() {
	}

	@Override
	public void stateFinished() {
		stop();
	}

	@Override
	public void statePaused(AvoiderState currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(AvoiderState currentState) {
	}

	@Override
	public void stateTransitioned(AvoiderState nextState) {
	}

	public enum AvoiderState implements State<CollisionAvoider, AvoiderState> {

		STARE {
			@Override
			public void execute(CollisionAvoider avoider) {
				avoider.stare();
			}
		},

		DRIVE {
			@Override
			public void execute(CollisionAvoider avoider) {
				avoider.driveToCorridor();
			}
		},

		DRIVING {
			@Override
			public void execute(CollisionAvoider avoider) {
				avoider.drivingToCorridor();
			}
		},

		WAITING {
			@Override
			public void execute(CollisionAvoider avoider) {
				avoider.waitInCorridor();
			}
		},

		FINISH {
			@Override
			public void execute(CollisionAvoider avoider) {
				avoider.finish();
			}
		}

	}

}
