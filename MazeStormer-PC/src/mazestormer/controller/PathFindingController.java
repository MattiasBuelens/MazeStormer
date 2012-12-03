package mazestormer.controller;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.robot.Navigator;
import mazestormer.robot.Robot;
import mazestormer.util.LongPoint;

public class PathFindingController extends SubController implements
		IPathFindingController {

	private TileSequenceRunner runner;

	public PathFindingController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
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

	private void postState(EventType eventType) {
		postEvent(new ActionEvent(eventType));
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
			setMaze(getSourceMaze());
			log("The maze is set to the source maze.");
		} else {
			log("There is no source maze available.");
		}
	}

	public class TileSequenceRunner implements Runnable {

		private Robot robot;
		private Maze maze;
		private Tile goal;
		private Tile[] tiles;
		private Navigator navigator;
		private boolean isRunning = false;

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
		public TileSequenceRunner(Robot robot, Maze maze, Tile goal,
				boolean singleStep) {
			this.robot = robot;
			this.maze = maze;
			this.goal = goal;
			this.tiles = this.maze.getMesh().findTilePath(getStartTile(),
					this.goal);
			this.singleStep = singleStep;
			initializeNavigator();
		}

		/**
		 * Create a new tile sequence runner with given robot, maze and tile
		 * sequence.
		 * 
		 * @pre If the tile sequence doesn't refer the null reference, the
		 *      robot's current tile must be the first tile in the sequence.
		 * @pre If the tile sequence doesn't refer the null reference, every two
		 *      consecutive tiles must be located next to each other in a four
		 *      way grid structure. (North, East, South, West)
		 * @param robot
		 *            The robot who must follow a tile sequence.
		 * @param maze
		 *            The maze the robot is positioned in.
		 * @param tiles
		 *            The tile sequence the robot must follow.
		 */
		public TileSequenceRunner(Robot robot, Maze maze, Tile[] tiles,
				boolean singleStep) {
			this.robot = robot;
			this.maze = maze;
			this.tiles = tiles;
			this.goal = this.tiles[this.tiles.length - 1];
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
			this.navigator = new Navigator(this.robot.getPilot(),
					this.robot.getPoseProvider());
			addWayPoints();
		}

		public void start() {
			this.isRunning = true;
			new Thread(this).start();
			postState(EventType.STARTED);
		}

		public void stop() {
			if (isRunning()) {
				this.isRunning = false;
				this.navigator.stop();
				postState(EventType.STOPPED);
			}
		}

		public synchronized boolean isRunning() {
			return this.isRunning;
		}

		private Tile getStartTile() {
			// Get absolute robot pose
			Pose pose = this.robot.getPoseProvider().getPose();
			// Get tile underneath robot
			Point relativePosition = this.maze.toRelative(pose.getLocation());
			Point tilePosition = this.maze.toTile(relativePosition);
			return this.maze.getTileAt(tilePosition);
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
				while (!this.navigator.waitForStop())
					Thread.yield();
			}

			stop();
		}

		private void addWayPoints() {
			for (int i = 1; i < this.tiles.length; i++) {
				Point tilePosition = this.tiles[i].getPosition().toPoint();
				Point absolutePosition = this.maze.toAbsolute(tilePosition);
				Waypoint w = new Waypoint((absolutePosition.x + 0.5)
						* this.maze.getTileSize(), (absolutePosition.y + 0.5)
						* this.maze.getTileSize());
				this.navigator.addWaypoint(w);
			}
		}

	}
}
