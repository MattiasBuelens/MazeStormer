package mazestormer.controller;

import static com.google.common.base.Preconditions.checkState;

import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.cli.ParseException;

import lejos.robotics.navigation.Pose;
import mazestormer.cli.CommandLineConfiguration;
import mazestormer.connect.ConnectEvent;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.maze.CombinedMaze;
import mazestormer.maze.IMaze;
import mazestormer.player.RelativePlayer;
import mazestormer.robot.ControllableRobot;
import mazestormer.simulator.VirtualRobot;
import mazestormer.simulator.collision.CollisionListener;
import mazestormer.ui.MainView;
import mazestormer.world.World;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class MainController implements IMainController {

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					start(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void start(String[] args) throws Exception {
		// Controller
		IMainController controller = new MainController();
		try {
			new CommandLineConfiguration(controller).parse(args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

		// View
		MainView view = new MainView(controller);
		view.registerEventBus(controller.getEventBus());
		view.setVisible(true);
	}

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("MainController-%d").build();
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

	private final World world;

	private RelativePlayer personalPlayer;
	public static final String defaultPlayerName = "Brons";

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
	private IGameController gameControl;
	private IGameSetUpController gameSetUpControl;

	private IStateController state;

	public MainController() {
		// Create event bus on named executor
		ExecutorService executor = Executors.newSingleThreadExecutor(factory);
		registerEventBus(new AsyncEventBus(getClass().getSimpleName(), executor));

		// Player and world
		IMaze personalMaze = new CombinedMaze();
		this.personalPlayer = new RelativePlayer(defaultPlayerName, null, personalMaze);
		this.world = new World(personalPlayer);
		gameControl().addPlayer(personalPlayer);

		// Connection
		connectionProvider = new ConnectionProvider();
		// TODO Configure device name in GUI?
		connectionContext.setDeviceName("brons");
		connectionContext.setWorld(getWorld());

		// Post initialized
		postEvent(new InitializeEvent());
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void registerEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
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
	public IPlayerMapController map() {
		return gameControl().getPersonalPlayerController().map();
	}

	@Override
	public ILogController log() {
		return gameControl().getPersonalPlayerController().log();
	}

	@Override
	public IStateController state() {
		if (state == null) {
			state = new StateController(this);
		}
		return state;
	}

	@Override
	public IGameController gameControl() {
		if (gameControl == null) {
			gameControl = new GameController(this);
		}
		return gameControl;
	}

	@Override
	public IGameSetUpController gameSetUpControl() {
		if (gameSetUpControl == null) {
			gameSetUpControl = new GameSetUpController(this);
		}
		return gameSetUpControl;
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
		getPlayer().getLogger().info("Initialized.");
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
			getPlayer().getLogger().info("Connected to robot.");
		} else {
			getPlayer().getLogger().info("Disconnected from robot.");
		}
	}

	/*
	 * Robot
	 */

	public ControllableRobot getControllableRobot() throws IllegalStateException {
		checkState(isConnected());
		ControllableRobot r = connector.getRobot();
		getPlayer().setRobot(r);
		return r;
	}

	@Subscribe
	public void registerCollisionListener(ConnectEvent e) {
		/*
		 * TODO Remove explicit cast to VirtualRobot by defining the collision
		 * detector and observer on the Robot interface. PhysicalRobot should
		 * provide a physical collision detector or a dummy implementation.
		 */
		if (e.isConnected() && getControllableRobot() instanceof VirtualRobot) {
			VirtualRobot vRobot = (VirtualRobot) getControllableRobot();
			vRobot.getCollisionObserver().addCollisionListener(new CollisionListener() {
				@Override
				public void brutalCrashOccured() {
					getPlayer().getLogger().severe("A collision occured, please retreat.");
				}
			});
		}
	}

	/*
	 * Robot pose
	 */

	public Pose getPose() {
		if (isConnected()) {
			return getControllableRobot().getPoseProvider().getPose();
		} else {
			return new Pose();
		}
	}

	/*
	 * Player
	 */

	@Override
	public RelativePlayer getPlayer() {
		return personalPlayer;
	}

	/*
	 * World
	 */

	@Override
	public World getWorld() {
		return world;
	}

}
