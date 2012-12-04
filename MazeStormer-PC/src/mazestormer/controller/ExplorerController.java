package mazestormer.controller;

import lejos.robotics.RangeScanner;
import mazestormer.controller.ExplorerEvent.EventType;
import mazestormer.maze.Maze;
import mazestormer.robot.Robot;
import mazestormer.robot.RunnerListener;

public class ExplorerController extends SubController implements
		IExplorerController {

	private ExplorerRunner runner;

	public ExplorerController(MainController mainController) {
		super(mainController);
	}

	private Robot getRobot() {
		return getMainController().getRobot();
	}

	private Maze getMaze() {
		return getMainController().getMaze();
	}

	RangeScanner getRangeScanner() {
		return getMainController().getRobot().getRangeScanner();
	}

	void postState(EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		runner = new ExplorerRunner(getRobot(), getMaze());
		runner.addListener(new ExplorerListener());
		runner.start();
	}

	@Override
	public void stopExploring() {
		if (runner != null) {
			runner.cancel();
			runner = null;
		}
	}

	private class ExplorerListener implements RunnerListener {
		@Override
		public void onStarted() {
			postState(EventType.STARTED);
		}

		@Override
		public void onCancelled() {
			postState(EventType.STOPPED);
		}
	}

}
