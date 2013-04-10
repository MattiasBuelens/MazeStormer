package mazestormer.controller;

import mazestormer.polygon.PolygonEvent;
import mazestormer.polygon.PolygonDriver;
import mazestormer.robot.Pilot;
import mazestormer.robot.StopEvent;
import mazestormer.state.AbstractStateListener;

import com.google.common.eventbus.Subscribe;

public class PolygonControlController extends SubController implements
		IPolygonControlController {

	public PolygonControlController(MainController mainController) {
		super(mainController);
	}

	private PolygonDriver polygonDriver;

	private Pilot getPilot() {
		return getMainController().getControllableRobot().getPilot();
	}

	@Override
	public void startPolygon(int nbSides, double sideLength, Direction direction) {
		polygonDriver = new PolygonDriver(getPilot(), nbSides, sideLength, direction);
		polygonDriver.addStateListener(new PolygonListener());
		polygonDriver.start();
	}

	@Override
	public void stopPolygon() {
		if (polygonDriver != null) {
			polygonDriver.stop();
			polygonDriver = null;
		}
	}

	@Override
	public IParametersController parameters() {
		return getMainController().parameters();
	}

	@Subscribe
	public void onStopped(StopEvent e) {
		stopPolygon();
	}

	private void postState(PolygonEvent.EventType eventType) {
		postEvent(new PolygonEvent(eventType));
	}

	private class PolygonListener extends
			AbstractStateListener<PolygonDriver.PolygonState> {

		@Override
		public void stateStarted() {
			postState(PolygonEvent.EventType.STARTED);
		}

		@Override
		public void stateStopped() {
			postState(PolygonEvent.EventType.STOPPED);
		}

		@Override
		public void stateFinished() {
			stateStopped();
		}

	}

}
