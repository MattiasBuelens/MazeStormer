package mazestormer.controller;

import java.awt.EventQueue;

import mazestormer.robot.Robot;
import mazestormer.ui.MainView;

import com.google.common.eventbus.EventBus;

public class MainController {

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

	/*
	 * View
	 */
	private MainView view;

	public MainController() {
		view = new MainView(this);
		view.registerEventBus(eventBus);
		view.setVisible(true);
	}

	public Robot getRobot() {
		return robot;
	}

}
