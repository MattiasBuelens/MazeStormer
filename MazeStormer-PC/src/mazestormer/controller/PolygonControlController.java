package mazestormer.controller;

import mazestormer.polygon.PolygonEvent;
import mazestormer.polygon.PolygonRunner;
import mazestormer.robot.Pilot;
import mazestormer.robot.StopEvent;
import mazestormer.state.AbstractStateListener;

import com.google.common.eventbus.Subscribe;

public class PolygonControlController extends SubController implements
		IPolygonControlController {

	public PolygonControlController(MainController mainController) {
		super(mainController);
	}

	private PolygonRunner runner;

	private Pilot getPilot() {
		return getMainController().getControllableRobot().getPilot();
	}

	@Override
	public void startPolygon(int nbSides, double sideLength, Direction direction) {
		runner = new PolygonRunner(getPilot(), nbSides, sideLength, direction);
		runner.addStateListener(new PolygonListener());
		runner.start();
	}

	@Override
	public void stopPolygon() {
		if (runner != null) {
			runner.stop();
			runner = null;
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
			AbstractStateListener<PolygonRunner.PolygonState> {

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
