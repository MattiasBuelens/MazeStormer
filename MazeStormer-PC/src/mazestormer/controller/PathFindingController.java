package mazestormer.controller;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.line.LineFinder;
import mazestormer.maze.IMaze;
import mazestormer.maze.PathFinder;
import mazestormer.maze.Tile;
import mazestormer.player.Player;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.Navigator;
import mazestormer.robot.NavigatorListener;
import mazestormer.robot.Robot;
import mazestormer.state.DefaultStateListener;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;
import mazestormer.util.LongPoint;

public class PathFindingController extends SubController implements IPathFindingController {

	private TileNavigator tileNavigator;

	public PathFindingController(MainController mainController) {
		super(mainController);
	}

	private Player getPlayer() {
		return getMainController().getPlayer();
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private IMaze getMaze() {
		return getPlayer().getMaze();
	}

	private IMaze getSourceMaze() {
		return getMainController().getWorld().getMaze();
	}

	private void log(String logText) {
		getPlayer().getLogger().info(logText);
	}

	private void postState(PathFinderEvent.EventType eventType) {
		postEvent(new PathFinderEvent(eventType));
	}

	@Override
	public void startStepAction(long goalX, long goalY) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.tileNavigator = new TileNavigator(getRobot(), getMaze(), goalTile, true, false);
		this.tileNavigator.start();
	}

	@Override
	public void startAction(long goalX, long goalY) {
		startAction(goalX, goalY, false, false);
	}

	@Override
	public void startAction(long goalX, long goalY, boolean singleStep, boolean reposition) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.tileNavigator = new TileNavigator(getRobot(), getMaze(), goalTile, singleStep, reposition);
		this.tileNavigator.start();
	}

	@Override
	public void stopAction() {
		if (this.tileNavigator != null) {
			this.tileNavigator.stop();
			this.tileNavigator = null;
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
			getMaze().importTiles(getSourceMaze().getTiles());
			log("The maze is set to the source maze.");
		} else {
			log("There is no source maze available.");
		}
	}

	public class TileNavigator extends StateMachine<TileNavigator, TileSequenceState> implements
			StateListener<TileSequenceState>, NavigatorListener {

		private final Robot robot;
		private final LineFinder lineFinder;
		private final Navigator navigator;
		private final PathFinder pathFinder;

		private Tile goal;
		private boolean singleStep;
		private boolean reposition;

		/**
		 * Create a new tile navigator with given robot, maze and goal tile.
		 * 
		 * @param robot
		 *            The robot who must follow a tile sequence.
		 * @param maze
		 *            The maze the robot is positioned in.
		 * @param goal
		 *            The target tile.
		 * @param singleStep
		 *            Whether to move one step or follow the whole path.
		 * @param reposition
		 *            Whether to reposition the robot before navigating.
		 */
		public TileNavigator(ControllableRobot robot, IMaze maze, Tile goal, boolean singleStep, boolean reposition) {
			this.robot = checkNotNull(robot);
			addStateListener(this);

			this.goal = goal;
			this.singleStep = singleStep;
			this.reposition = reposition;

			// Navigator
			this.navigator = new Navigator(robot.getPilot(), robot.getPoseProvider());
			navigator.addNavigatorListener(this);

			// Path finder
			this.pathFinder = new PathFinder(maze);

			// Line finder
			this.lineFinder = new LineFinder(getPlayer());
			lineFinder.addStateListener(new DefaultStateListener<LineFinder.LineFinderState>() {
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

	protected enum TileSequenceState implements State<TileNavigator, TileSequenceState> {
		LINE_FINDER {
			@Override
			public void execute(TileNavigator navigator) {
				navigator.startLineFinder();
			}
		},
		NAVIGATOR {
			@Override
			public void execute(TileNavigator navigator) {
				navigator.startNavigator();
			}
		}
	}

}
