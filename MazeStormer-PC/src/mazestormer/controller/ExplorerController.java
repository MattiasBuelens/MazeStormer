package mazestormer.controller;

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

	private void log(String logText) {
		getMainController().getLogger().info(logText);
	}

	private void postState(ExplorerEvent.EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		runner = new ExplorerRunner(getRobot(), getMaze()) {
			@Override
			protected void log(String message) {
				super.log(message);
				ExplorerController.this.log(message);
			}
		};
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
			postState(ExplorerEvent.EventType.STARTED);
		}

		@Override
		public void onCompleted() {
			onCancelled();
		}

		@Override
		public void onCancelled() {
			postState(ExplorerEvent.EventType.STOPPED);
		}

	}

}
