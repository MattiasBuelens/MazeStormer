package mazestormer.controller;

import mazestormer.connect.ConnectEvent;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.RunnerListener;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements
		ILineFinderController {

	private LineFinderRunner runner;

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private ControllableRobot getRobot() {
		return getMainController().getControllableRobot();
	}

	private CalibratedLightSensor getLightSensor() {
		return getMainController().getControllableRobot().getLightSensor();
	}

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLightSensor().setFloodlight(true);
		}
	}

	@Override
	public void startSearching() {
		runner = new LineFinderRunner(getRobot()) {
			@Override
			protected void log(String message) {
				LineFinderController.this.log(message);
			}
		};
		runner.addListener(new LineFinderListener());
		runner.start();
	}

	@Override
	public void stopSearching() {
		if (runner != null) {
			runner.shutdown();
			runner = null;
		}
	}

	private void postState(LineFinderEvent.EventType eventType) {
		postEvent(new LineFinderEvent(eventType));
	}

	private class LineFinderListener implements RunnerListener {

		@Override
		public void onStarted() {
			// Post state
			postState(LineFinderEvent.EventType.STARTED);
		}

		@Override
		public void onCompleted() {
			// Post state
			postState(LineFinderEvent.EventType.STOPPED);
		}

		@Override
		public void onCancelled() {
			// Post state
			postState(LineFinderEvent.EventType.STOPPED);
		}

	}
}
