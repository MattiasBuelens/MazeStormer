package mazestormer.controller;

import java.awt.EventQueue;

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

	private IMapController map;
	private IStateController state;

	/*
	 * View
	 */
	private EventSource view;

	public MainController() {
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

	private void postEvent(Object event) {
		getEventBus().post(event);
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
	public IMapController map() {
		if (map == null) {
			map = new MapController(this);
		}
		return map;
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
	 * Connection
	 */

	public boolean isConnected() {
		return connector != null && connector.isConnected();
	}

	public void connect(RobotType robotType) throws IllegalStateException {
		if (isConnected())
			throw new IllegalStateException("Already connected.");

		connector = connectionProvider.getConnector(robotType);
		connector.connect();
		postConnected();
	}

	public void disconnect() throws IllegalStateException {
		if (!isConnected())
			throw new IllegalStateException("Already disconnected.");

		connector.disconnect();
		connector = null;
		postConnected();
	}

	private void postConnected() {
		postEvent(new ConnectEvent(isConnected()));
	}

	// Post connected state on initialize
	@Subscribe
	public void onInitialized(InitializeEvent e) {
		postConnected();
	}

	/*
	 * Robot
	 */

	public Robot getRobot() throws IllegalStateException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to robot.");
		return connector.getRobot();
	}

	@Subscribe
	public void registerRobotMoveListener(ConnectEvent e) {
		if (e.isConnected()) {
			connector.getRobot().addMoveListener(new MovePublisher());
		}
	}

	private class MovePublisher implements MoveListener {

		@Override
		public void moveStarted(Move event, MoveProvider mp) {
			postEvent(new MoveEvent(MoveEvent.EventType.STARTED, event));
		}

		@Override
		public void moveStopped(Move event, MoveProvider mp) {
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
			poseProvider = new OdometryPoseProvider(getRobot());
		} else {
			poseProvider = null;
		}
	}

}
