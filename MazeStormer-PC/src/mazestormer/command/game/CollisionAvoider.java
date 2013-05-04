package mazestormer.command.game;

import java.util.ArrayList;
import java.util.List;

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

	public static final long MINIMUM_TIMEOUT = 2000;
	public static final long MAXIMUM_TIMEOUT = 10000;

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
		// TODO Check whether reading within certain range?
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

	protected void driveToCorridor() {
		log("Robot detected, avoiding collision");

		// Get current and blocked tile
		Tile currentTile = getDriver().getCurrentTile();
		Orientation heading = getDriver().getRobotHeading();
		Tile blockedTile = getMaze().getOrCreateNeighbor(currentTile, heading);

		// Find path to corridor
		List<Tile> path = null;
		path = findPathToCorridor(currentTile, blockedTile);
		if (path.isEmpty()) {
			// Cannot move, simply wait
			transition(AvoiderState.WAITING);
		} else {
			// Follow path
			getDriver().skipCurrentBarcode(true);
			getDriver().followPath(path);
			transition(AvoiderState.DRIVING);
		}
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

	protected void drivingToCorridor() {
		log("Driving to corridor");
	}

	protected void waitInCorridor() {
		// Wait for a random time
		WaitFuture<?> future = new WaitFuture<Object>();
		long timeout = getRandomTimeout();
		log("Waiting for " + timeout + " ms in corridor");

		bindTransition(future, AvoiderState.FINISH);
		future.resolveAfter(null, timeout);
	}

	private long getRandomTimeout() {
		return MINIMUM_TIMEOUT + (long) (Math.random() * (MAXIMUM_TIMEOUT - MINIMUM_TIMEOUT + 1));
	}

	@Override
	public void stateStarted() {
		transition(AvoiderState.DRIVE);
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
