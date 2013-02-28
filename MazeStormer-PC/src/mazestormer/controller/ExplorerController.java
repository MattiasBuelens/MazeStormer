package mazestormer.controller;

import mazestormer.explore.ExplorerEvent;
import mazestormer.explore.ExplorerRunner;
import mazestormer.player.Player;
import mazestormer.state.AbstractStateListener;

public class ExplorerController extends SubController implements IExplorerController {

	private ExplorerRunner runner;
	private boolean isLineAdjustEnabled = true;
	private int lineAdjustInterval = 10;

	public ExplorerController(MainController mainController) {
		super(mainController);
	}

	@Override
	public IBarcodeController getBarcodeController() {
		return getMainController().barcodeControl();
	}

	@Override
	public IParametersController getParametersController() {
		return getMainController().parameters();
	}

	@Override
	public ICheatController getCheatController() {
		return getMainController().cheatControl();
	}

	private Player getPlayer() {
		return getMainController().getPlayer();
	}

	private void log(String logText) {
		getMainController().getPlayer().getLogger().info(logText);
	}

	private void postState(ExplorerEvent.EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		runner = new ExplorerRunner(getPlayer()) {
			@Override
			protected void log(String message) {
				ExplorerController.this.log(message);
			}
		};
		// Set parameters
		runner.setLineAdjustEnabled(isLineAdjustEnabled());
		runner.setLineAdjustInterval(getLineAdjustInterval());
		runner.setScanSpeed(getBarcodeController().getScanSpeed());
		// Start
		runner.addStateListener(new ExplorerListener());
		runner.start();
	}

	@Override
	public void stopExploring() {
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}

	@Override
	public boolean isLineAdjustEnabled() {
		return isLineAdjustEnabled;
	}

	@Override
	public void setLineAdjustEnabled(boolean isEnabled) {
		isLineAdjustEnabled = isEnabled;
		if (runner != null) {
			runner.setLineAdjustEnabled(isEnabled);
		}
	}

	@Override
	public int getLineAdjustInterval() {
		return lineAdjustInterval;
	}

	@Override
	public void setLineAdjustInterval(int interval) {
		lineAdjustInterval = interval;
		if (runner != null) {
			runner.setLineAdjustInterval(interval);
		}
	}

	private class ExplorerListener extends AbstractStateListener<ExplorerRunner.ExplorerState> {

		@Override
		public void stateStarted() {
			postState(ExplorerEvent.EventType.STARTED);
		}

		@Override
		public void stateStopped() {
			postState(ExplorerEvent.EventType.STOPPED);
		}

		@Override
		public void stateFinished() {
			stateStopped();
		}

	}

}
