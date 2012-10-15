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
	private IParametersController parameters;
	private IManualControlController manualControl;
	private IPolygonControlController polygonControl;

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
	public IParametersController parameters() {
		if (parameters == null) {
			parameters = new ParametersController(this);
			parameters.registerEventBus(getEventBus());
		}
		return parameters;
	}

	@Override
	public IManualControlController manualControl() {
		if (manualControl == null) {
			manualControl = new ManualControlController(this);
			manualControl.registerEventBus(getEventBus());
		}
		return manualControl;
	}

	@Override
	public IPolygonControlController polygonControl() {
		if (polygonControl == null) {
			polygonControl = new PolygonControlController(this);
			polygonControl.registerEventBus(getEventBus());
		}
		return polygonControl;
	}

}
