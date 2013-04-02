package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.line.LineFinderRunner;
import mazestormer.maze.IMaze;
import mazestormer.maze.Maze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Navigator;
import mazestormer.robot.NavigatorListener;
import mazestormer.robot.Robot;
import mazestormer.state.AbstractStateListener;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.LongPoint;

public class PathFindingController extends SubController implements IPathFindingController {

	private TileSequenceRunner runner;

	public PathFindingController(MainController mainController) {
		super(mainController);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private IMaze getMaze() {
		return getMainController().getPlayer().getMaze();
	}

	private Maze getSourceMaze() {
		return getMainController().getWorld().getMaze();
	}

	private void log(String logText) {
		getMainController().getPlayer().getLogger().info(logText);
	}

	private void postState(PathFinderEvent.EventType eventType) {
		postEvent(new PathFinderEvent(eventType));
	}

	@Override
	public void startStepAction(long goalX, long goalY) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile, true, false);
		this.runner.start();
	}

	@Override
	public void startAction(long goalX, long goalY) {
		startAction(goalX, goalY, false, false);
	}

	@Override
	public void startAction(long goalX, long goalY, boolean singleStep, boolean reposition) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile, singleStep, reposition);
		this.runner.start();
	}

	@Override
	public void stopAction() {
		if (this.runner != null) {
			this.runner.stop();
			this.runner = null;
		}
	}

	@Override
	public long getCurrentTileX() {
		return getCurrentTile().getX();
	}

	@Override
	public long getCurrentTileY() {
		return getCurrentTile().getY();
	}

	private Tile getCurrentTile() {
		// Get absolute robot pose
		Pose pose = getRobot().getPoseProvider().getPose();
		// Get tile underneath robot
		Point relativePosition = getMaze().toRelative(pose.getLocation());
		Point tilePosition = getMaze().toTile(relativePosition);
		return getMaze().getTileAt(tilePosition);
	}

	@Override
	public long getTileMinX() {
		return getMaze().getMinX();
	}

	@Override
	public long getTileMinY() {
		return getMaze().getMinY();
	}

	@Override
	public long getTileMaxX() {
		return getMaze().getMaxX();
	}

	@Override
	public long getTileMaxY() {
		return getMaze().getMaxY();
	}

	@Override
	public void addSourceMaze() {
		if (getSourceMaze().getTiles().size() > 1) {
			getMaze().updateTiles(getSourceMaze().getTiles());
			log("The maze is set to the source maze.");
		} else {
			log("There is no source maze available.");
		}
	}

	public class TileSequenceRunner extends StateMachine<TileSequenceRunner, TileSequenceState> implements
			StateListener<TileSequenceState>, NavigatorListener {

		private final Robot robot;
		private final LineFinderRunner lineFinder;
		private final Navigator navigator;
		private final PathFinder pathFinder;

		private Tile goal;
		private boolean singleStep;
		private boolean reposition;

		/**
		 * Create a new tile sequence runner with given robot, maze and goal
		 * tile.
		 * 
		 * @param robot
		 *            The robot who must follow a tile sequence.
		 * @param iMaze
		 *            The maze the robot is positioned in.
		 * @param goal
		 *            The target tile.
		 * @param singleStep
		 *            Whether to move one step or follow the whole path.
		 * @param reposition
		 *            Whether to reposition the robot before navigating.
		 */
		public TileSequenceRunner(ControllableRobot robot, IMaze iMaze, Tile goal, boolean singleStep,
				boolean reposition) {
			this.robot = checkNotNull(robot);
			addStateListener(this);

			this.goal = goal;
			this.singleStep = singleStep;
			this.reposition = reposition;

			// Navigator
			this.navigator = new Navigator(robot.getPilot(), robot.getPoseProvider());
			navigator.addNavigatorListener(this);

			// Path finder
			this.pathFinder = new PathFinder(iMaze);

			// Line finder
			this.lineFinder = new LineFinderRunner(getRobot()) {
				@Override
				protected void log(String message) {
					PathFindingController.this.log(message);
				}
			};
			lineFinder.addStateListener(new AbstractStateListener<LineFinderRunner.LineFinderState>() {
				@Override
				public void stateFinished() {
					transition(TileSequenceState.NAVIGATOR);
				}
			});
		}

		private void init() {
			// Set path
			Tile startTile = pathFinder.getTileAt(robot.getPoseProvider().getPose());
			List<Waypoint> path = pathFinder.findPath(startTile, goal);
			if (singleStep && !path.isEmpty()) {
				// Retain just the first way point
				Waypoint step = path.get(0);
				path.clear();
				path.add(step);
			}
			navigator.setPath(path);

			// Transition
			if (reposition) {
				transition(TileSequenceState.LINE_FINDER);
			} else {
				transition(TileSequenceState.NAVIGATOR);
			}
		}

		private void startLineFinder() {
			lineFinder.start();
		}

		private void startNavigator() {
			navigator.start();
		}

		@Override
		public void stateStarted() {
			// Post state
			postState(PathFinderEvent.EventType.STARTED);
			// Start
			init();
		}

		@Override
		public void stateStopped() {
			// Clean up
			lineFinder.stop();
			navigator.stop();
			// Post state
			postState(PathFinderEvent.EventType.STOPPED);
		}

		@Override
		public void stateFinished() {
			stateStopped();
		}

		@Override
		public void statePaused(TileSequenceState currentState, boolean onTransition) {
		}

		@Override
		public void stateResumed(TileSequenceState currentState) {
		}

		@Override
		public void stateTransitioned(TileSequenceState nextState) {
		}

		@Override
		public void navigatorStarted(Pose pose) {
		}

		@Override
		public void navigatorStopped(Pose pose) {
			stop();
		}

		@Override
		public void navigatorCompleted(Waypoint waypoint, Pose pose) {
			finish();
		}

		@Override
		public void navigatorPaused(Navigator.NavigatorState currentState, Pose pose, boolean onTransition) {
			stop();
		}

		@Override
		public void navigatorResumed(Navigator.NavigatorState currentState, Pose pose) {
		}

		@Override
		public void navigatorAtWaypoint(Waypoint waypoint, Pose pose) {
		}

	}

	protected enum TileSequenceState implements State<TileSequenceRunner, TileSequenceState> {
		LINE_FINDER {
			@Override
			public void execute(TileSequenceRunner runner) {
				runner.startLineFinder();
			}
		},
		NAVIGATOR {
			@Override
			public void execute(TileSequenceRunner runner) {
				runner.startNavigator();
			}
		}
	}

}
