package mazestormer.controller;

import mazestormer.connect.ConnectEvent;
import mazestormer.line.LineFinder;
import mazestormer.line.LineFinderEvent;
import mazestormer.player.Player;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.state.DefaultStateListener;

import com.google.common.eventbus.Subscribe;

public class LineFinderController extends SubController implements ILineFinderController {

	private LineFinder lineFinder;

	public LineFinderController(MainController mainController) {
		super(mainController);
	}

	private Player getPlayer() {
		return getMainController().getPlayer();
	}

	private CalibratedLightSensor getLightSensor() {
		return getMainController().getControllableRobot().getLightSensor();
	}

	@Subscribe
	public void onConnect(ConnectEvent e) {
		if (e.isConnected()) {
			getLightSensor().setFloodlight(true);
		}
	}

	@Override
	public void startSearching() {
		lineFinder = new LineFinder(getPlayer());
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

	private class LineFinderListener extends DefaultStateListener<LineFinder.LineFinderState> {

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
