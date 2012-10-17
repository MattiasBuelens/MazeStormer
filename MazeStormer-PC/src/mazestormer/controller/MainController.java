package mazestormer.controller;

import java.awt.EventQueue;

import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.Robot;
import mazestormer.ui.MainView;
import mazestormer.util.EventSource;

import com.google.common.eventbus.EventBus;

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

		getEventBus().post(new InitializeEvent());
	}

	protected EventSource createView() {
		MainView view = new MainView(this);
		view.setVisible(true);
		return view;
	}

	private EventBus getEventBus() {
		return eventBus;
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

	public Connector getConnector() {
		return connector;
	}

	public Connector setConnector(RobotType robotType) {
		connector = connectionProvider.getConnector(robotType);
		return connector;
	}

	public boolean isConnected() {
		return getConnector() != null && getConnector().isConnected();
	}

	public Robot getRobot() throws IllegalStateException {
		if (!isConnected())
			throw new IllegalStateException("Not connected to robot.");
		return getConnector().getRobot();
	}

}
