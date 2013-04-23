package mazestormer.controller;

import mazestormer.connect.ConnectEvent;
import mazestormer.line.LineFinderEvent;
import mazestormer.line.LineFinder;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.state.AbstractStateListener;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements
		ILineFinderController {

	private LineFinder lineFinder;

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
		getMainController().getPlayer().getLogger().info(logText);
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLightSensor().setFloodlight(true);
		}
	}

	@Override
	public void startSearching() {
		lineFinder = new LineFinder(getRobot()) {
			@Override
			protected void log(String message) {
				LineFinderController.this.log(message);
			}
		};
		lineFinder.addStateListener(new LineFinderListener());
		lineFinder.start();
	}

	@Override
	public void stopSearching() {
		if (lineFinder != null) {
			lineFinder.stop();
			lineFinder = null;
		}
	}

	private void postState(LineFinderEvent.EventType eventType) {
		postEvent(new LineFinderEvent(eventType));
	}

	private class LineFinderListener extends
			AbstractStateListener<LineFinder.LineFinderState> {

		@Override
		public void stateStarted() {
			postState(LineFinderEvent.EventType.STARTED);
		}

		@Override
		public void stateStopped() {
			postState(LineFinderEvent.EventType.STOPPED);
		}

		@Override
		public void stateFinished() {
			stateStopped();
		}

	}
}
