package mazestormer.controller;

import static com.google.common.base.Preconditions.*;

import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.MoveEvent;
import mazestormer.robot.Robot;
import mazestormer.ui.MainView;
import mazestormer.util.EventSource;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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

	/*
	 * Events
	 */
	private EventBus eventBus = new EventBus(getClass().getSimpleName());

	/*
	 * Models
	 */
	private ConnectionProvider connectionProvider;
	private Connector connector;
	private PoseProvider poseProvider;

	/*
	 * Controllers
	 */
	private IConfigurationController configuration;
	private IParametersController parameters;
	private IManualControlController manualControl;
	private IPolygonControlController polygonControl;
	private ILineFinderController lineFinderControl;

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
		getEventBus().register(this);

		connectionProvider = new ConnectionProvider();

		view = createView();
		view.registerEventBus(getEventBus());

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
	public ILineFinderController lineFinderControl() {
		if (lineFinderControl == null) {
			lineFinderControl = new LineFinderController(this);
		}
		return lineFinderControl;
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
		connector.connect();
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

	/*
	 * Robot pose
	 */
	public Pose getPose() {
		if (poseProvider != null) {
			return poseProvider.getPose();
		} else {
			return new Pose();
		}
	}

	@Subscribe
	public void setupPoseProvider(ConnectEvent e) {
		if (e.isConnected()) {
			poseProvider = new OdometryPoseProvider(getRobot().getPilot());
			poseProvider.setPose(new Pose(0f, 0f, 90f));
		} else {
			poseProvider = null;
		}
	}

}
