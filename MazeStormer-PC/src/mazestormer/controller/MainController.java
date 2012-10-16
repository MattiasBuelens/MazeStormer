package mazestormer.controller;

import java.awt.EventQueue;

import mazestormer.robot.Robot;
import mazestormer.robot.SimulatedRobot;
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
	private Robot robot;

	/*
	 * Controllers
	 */
	private IConfigurationController configuration;
	private IParametersController parameters;
	private IManualControlController manualControl;
	private IPolygonControlController polygonControl;

	private IMapController map;

	/*
	 * View
	 */
	private EventSource view;

	public MainController() {
		robot = new SimulatedRobot(Robot.leftWheelDiameter,
				Robot.rightWheelDiameter, Robot.trackWidth);

		view = createView();
		view.registerEventBus(getEventBus());
	}

	public Robot getRobot() throws IllegalStateException {
		if (robot == null) {
			throw new IllegalStateException("No robot is connected.");
		}
		return robot;
	}

	private EventBus getEventBus() {
		return eventBus;
	}

	protected EventSource createView() {
		MainView view = new MainView(this);
		view.setVisible(true);
		return view;
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
	public void register(EventSource eventSource) {
		eventSource.registerEventBus(getEventBus());
	}

}
