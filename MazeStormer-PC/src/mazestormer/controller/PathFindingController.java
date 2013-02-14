package mazestormer.controller;

import java.util.List;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.robot.PathRunner;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.LongPoint;

public class PathFindingController extends SubController implements
		IPathFindingController {

	private TileSequenceRunner runner;

	public PathFindingController(MainController mainController) {
		super(mainController);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
	}

	private Maze getSourceMaze() {
		return getMainController().getSourceMaze();
	}

	private void setMaze(Maze maze) {
		getMainController().setMaze(maze);
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	private void postState(PathFinderEvent.EventType eventType) {
		postEvent(new PathFinderEvent(eventType));
	}

	@Override
	public void startStepAction(long goalX, long goalY) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile,
				true);
		this.runner.start();
	}

	@Override
	public void startAction(long goalX, long goalY) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile,
				false);
		this.runner.start();
	}

	@Override
	public void startAction(long goalX, long goalY, boolean singleStep,
			boolean reposition) {
		Tile goalTile = getMaze().getTileAt(new LongPoint(goalX, goalY));
		this.runner = new TileSequenceRunner(getRobot(), getMaze(), goalTile,
				singleStep);
		this.runner.setReposition(reposition);
		this.runner.start();
	}

	@Override
	public void stopAction() {
		if (this.runner != null) {
			this.runner.shutdown();
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
			setMaze(getSourceMaze());
			log("The maze is set to the source maze.");
		} else {
			log("There is no source maze available.");
		}
	}

	public class TileSequenceRunner extends PathRunner {

		private Tile goal;
		private boolean singleStep;
		private boolean reposition;

		/**
		 * Create a new tile sequence runner with given robot, maze and goal
		 * tile.
		 * 
		 * @param robot
		 *            The robot who must follow a tile sequence.
		 * @param maze
		 *            The maze the robot is positioned in.
		 * @param tiles
		 *            The tile sequence the robot must follow.
		 */
		public TileSequenceRunner(ControllableRobot robot, Maze maze, Tile goal,
				boolean singleStep) {
			super(robot, maze);
			this.goal = goal;
			this.singleStep = singleStep;
			initializeNavigator();
		}

		public void setSinglestep(boolean request) {
			this.singleStep = request;
		}

		public void setReposition(boolean request) {
			this.reposition = request;
		}

		private void initializeNavigator() {
			List<Waypoint> waypoints = findPath(goal);
			for (Waypoint waypoint : waypoints) {
				navigator.addWaypoint(waypoint);
			}
		}

		@Override
		public void onStarted() {
			super.onStarted();
			// Post state
			postState(PathFinderEvent.EventType.STARTED);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			// Post state
			postState(PathFinderEvent.EventType.STOPPED);
		}

		@Override
		public void run() {
			if (this.singleStep && this.reposition) {
				new LineFinderController(getMainController()).startSearching();
			}

			this.navigator.singleStep(this.singleStep);
			this.navigator.followPath();
			if (this.singleStep) {
				this.navigator.waitForStop();
			} else {
				while (!this.navigator.waitForStop()) {
					Thread.yield();
				}
			}

			// Done
			resolve();
		}

	}
}
