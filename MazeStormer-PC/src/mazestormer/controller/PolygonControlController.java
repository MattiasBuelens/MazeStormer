package mazestormer.controller;

import mazestormer.robot.Pilot;
import mazestormer.robot.Runner;
import mazestormer.robot.StopEvent;

import com.google.common.eventbus.Subscribe;

public class PolygonControlController extends SubController implements
		IPolygonControlController {

	public PolygonControlController(MainController mainController) {
		super(mainController);
	}

	private PolygonRunner runner;

	private Pilot getPilot() {
		return getMainController().getRobot().getPilot();
	}

	@Override
	public void startPolygon(int nbSides, double sideLength, Direction direction) {
		runner = new PolygonRunner(nbSides, sideLength, direction);
		runner.start();
	}

	@Override
	public void stopPolygon() {
		if (runner != null) {
			runner.cancel();
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

	private class PolygonRunner extends Runner {

		private final int nbSides;
		private final double sideLength;
		private final Direction direction;

		public PolygonRunner(int nbSides, double sideLength, Direction direction) {
			super(PolygonControlController.this.getPilot());
			this.nbSides = nbSides;
			this.sideLength = sideLength;
			this.direction = direction;
		}

		@Override
		public void onStarted() {
			super.onStarted();
			// Post state
			postState(PolygonEvent.EventType.STARTED);
		}

		@Override
		public void onCancelled() {
			super.onCancelled();
			// Post state
			postState(PolygonEvent.EventType.STOPPED);
		}

		@Override
		public void run() {
			int parity = (direction == Direction.ClockWise) ? -1 : 1;
			double angle = (double) parity * 360d / (double) nbSides;

			for (int i = 0; i < nbSides; ++i) {
				travel(sideLength);
				rotate(angle);
			}

			// Done
			cancel();
		}

	}

}
