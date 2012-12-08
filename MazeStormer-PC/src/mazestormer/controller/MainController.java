package mazestormer.controller;

import static com.google.common.base.Preconditions.checkState;

import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.maze.Maze;
import mazestormer.maze.Tile;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.Robot;
import mazestormer.simulator.VirtualRobot;
import mazestormer.simulator.collision.CollisionListener;
import mazestormer.ui.MainView;
import mazestormer.util.EventSource;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class MainController implements IMainController {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainController();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static final ThreadFactory factory = new ThreadFactoryBuilder()
			.setNameFormat("MainController-%d").build();
	/*
	 * Events
	 */
	private EventBus eventBus;

	/*
	 * Models
	 */
	private final ConnectionProvider connectionProvider;
	private final ConnectionContext connectionContext = new ConnectionContext();
	private Connector connector;

	private Maze maze;
	private Maze sourceMaze;
	private Tile goalTile;

	/*
	 * Controllers
	 */
	private IConfigurationController configuration;
	private IParametersController parameters;
	private ICalibrationController calibration;
	private IManualControlController manualControl;
	private IPolygonControlController polygonControl;
	private IBarcodeController barcodeControl;
	private IPathFindingController pathFindingControl;
	private ILineFinderController lineFinderControl;
	private IExplorerController explorerControl;
	private ICheatController cheatControl;

	private IMapController map;
	private ILogController log;

	private IStateController state;

	/*
	 * Logging
	 */
	private String logName = "MazeStormer";
	private Logger logger;

	/*
	 * View
	 */
	private EventSource view;

	public MainController() {
		// Create event bus on named executor
		ExecutorService executor = Executors.newSingleThreadExecutor(factory);
		eventBus = new AsyncEventBus(getClass().getSimpleName(), executor);

		// Register on event bus
		getEventBus().register(this);

		// Connection
		connectionProvider = new ConnectionProvider();
		// TODO Configure device name in GUI?
		connectionContext.setDeviceName("brons");
		connectionContext.setSourceMaze(getSourceMaze());

		// View
		view = createView();
		view.registerEventBus(getEventBus());

		// Post initialized
		postEvent(new InitializeEvent());
	}

	protected EventSource createView() {
		MainView view = new MainView(this);
		view.setVisible(true);
		return view;
	}

	private EventBus getEventBus() {
		return eventBus;
	}

	private void postEvent(final Object event) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				getEventBus().post(event);
			}
		});
	}

	@Override
	public IConfigurationController configuration() {
		if (configuration == null) {
			configuration = new ConfigurationController(this);
		}
		return configuration;
	}

	@Override
	public ICalibrationController calibration() {
		if (calibration == null)
			calibration = new CalibrationController(this);
		return calibration;
	}

	@Override
	public IParametersController parameters() {
		if (parameters == null) {
			parameters = new ParametersController(this);
		}
		return parameters;
	}

	@Override
	public IManualControlController manualControl() {
		if (manualControl == null) {
			manualControl = new ManualControlController(this);
		}
		return manualControl;
	}

	@Override
	public IPolygonControlController polygonControl() {
		if (polygonControl == null) {
			polygonControl = new PolygonControlController(this);
		}
		return polygonControl;
	}

	@Override
	public IBarcodeController barcodeControl() {
		if (barcodeControl == null) {
			barcodeControl = new BarcodeController(this);
		}
		return barcodeControl;
	}

	@Override
	public IPathFindingController pathFindingControl() {
		if (pathFindingControl == null) {
			pathFindingControl = new PathFindingController(this);
		}
		return pathFindingControl;
	}

	@Override
	public ILineFinderController lineFinderControl() {
		if (lineFinderControl == null) {
			lineFinderControl = new LineFinderController(this);
		}
		return lineFinderControl;
	}

	@Override
	public IExplorerController explorerControl() {
		if (explorerControl == null) {
			explorerControl = new ExplorerController(this);
		}
		return explorerControl;
	}
	
	@Override
	public ICheatController cheatControl() {
		if (cheatControl == null) {
			cheatControl = new CheatController(this);
		}
		return cheatControl;
	}

	@Override
	public IMapController map() {
		if (map == null) {
			map = new MapController(this);
		}
		return map;
	}

	@Override
	public ILogController log() {
		if (log == null) {
			log = new LogController(this);
		}
		return log;
	}

	@Override
	public IStateController state() {
		if (state == null) {
			state = new StateController(this);
		}
		return state;
	}

	@Override
	public void register(EventSource eventSource) {
		eventSource.registerEventBus(getEventBus());
	}

	/*
	 * Logging
	 */

	public String getLogName() {
		return logName;
	}

	public Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(getLogName());
			logger.setLevel(Level.ALL);
		}
		return logger;
	}

	/*
	 * Initialization
	 */

	// Post connected state on initialize
	@Subscribe
	public void onInitialized(InitializeEvent e) {
		postConnected();
	}

	@Subscribe
	public void logInitialize(InitializeEvent e) {
		getLogger().info("Initialized.");
	}

	/*
	 * Connection
	 */

	public boolean isConnected() {
		return connector != null && connector.isConnected();
	}

	public void connect(RobotType robotType) throws IllegalStateException {
		checkState(!isConnected());

		connector = connectionProvider.getConnector(robotType);
		connector.connect(connectionContext);
		postConnected();
	}

	public void disconnect() throws IllegalStateException {
		checkState(isConnected());

		connector.disconnect();
		connector = null;
		postConnected();
	}

	private void postConnected() {
		postEvent(new ConnectEvent(isConnected()));
	}

	@Subscribe
	public void logConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLogger().info("Connected to robot.");
		} else {
			getLogger().info("Disconnected from robot.");
		}
	}

	/*
	 * Robot
	 */

	public Robot getRobot() throws IllegalStateException {
		checkState(isConnected());
		return connector.getRobot();
	}

	@Subscribe
	public void registerPilotMoveListener(ConnectEvent e) {
		if (e.isConnected()) {
			getRobot().getPilot().addMoveListener(new MovePublisher());
		}
	}

	private class MovePublisher implements MoveListener {

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			getLogger().info("Move started: " + event.toString());
			postEvent(new MoveEvent(MoveEvent.EventType.STARTED, event));
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
			getLogger().info("Move stopped: " + event.toString());
			postEvent(new MoveEvent(MoveEvent.EventType.STOPPED, event));
		}

	}

	@Subscribe
	public void registerCollisionListener(ConnectEvent e) {
		/*
		 * TODO Remove explicit cast to VirtualRobot by defining the collision
		 * detector and observer on the Robot interface. PhysicalRobot should
		 * provide a physical collision detector or a dummy implementation.
		 */
		if (e.isConnected() && getRobot() instanceof VirtualRobot) {
			VirtualRobot vRobot = (VirtualRobot) getRobot();
			vRobot.getCollisionObserver().addCollisionListener(
					new CollisionListener() {
						@Override
						public void brutalCrashOccured() {
							getLogger().severe(
									"A collision occured, please retreat.");
						}
					});
		}
	}

	/*
	 * Robot pose
	 */

	private Pose getStartPose() {
		// return new Pose(0f, 0f, 90f);
		return new Pose(20f, 20f, 90f);
	}

	public Pose getPose() {
		if (isConnected()) {
			return getRobot().getPoseProvider().getPose();
		} else {
			return getStartPose();
		}
	}

	@Subscribe
	public void setupStartPose(ConnectEvent e) {
		if (e.isConnected()) {
			getRobot().getPoseProvider().setPose(getStartPose());
		}
	}

	/*
	 * Maze
	 */

	public Maze getMaze() {
		if (maze == null) {
			maze = new Maze();
		}
		return maze;
	}

	public Maze getSourceMaze() {
		if (sourceMaze == null) {
			sourceMaze = new Maze();
		}
		return sourceMaze;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public Tile getGoalTile() {
		return this.goalTile;
	}

	public void setGoalTile(Tile tile) {
		this.goalTile = tile;
	}
}
