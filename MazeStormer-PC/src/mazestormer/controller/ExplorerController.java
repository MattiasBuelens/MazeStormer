package mazestormer.controller;

import mazestormer.command.explore.Explorer;
import mazestormer.command.explore.ExplorerEvent;
import mazestormer.player.Player;
import mazestormer.state.AbstractStateListener;

public class ExplorerController extends SubController implements IExplorerController {

	private Explorer explorer;
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
		explorer = new Explorer(getPlayer()) {
			@Override
			protected void log(String message) {
				ExplorerController.this.log(message);
			}
		};
		// Set parameters
		explorer.setLineAdjustEnabled(isLineAdjustEnabled());
		explorer.setLineAdjustInterval(getLineAdjustInterval());
		explorer.setScanSpeed(getBarcodeController().getScanSpeed());
		// Start
		explorer.addStateListener(new ExplorerListener());
		explorer.start();
	}

	@Override
	public void stopExploring() {
		if (explorer != null) {
			explorer.stop();
			explorer = null;
		}
	}

	@Override
	public boolean isLineAdjustEnabled() {
		return isLineAdjustEnabled;
	}

	@Override
	public void setLineAdjustEnabled(boolean isEnabled) {
		isLineAdjustEnabled = isEnabled;
		if (explorer != null) {
			explorer.setLineAdjustEnabled(isEnabled);
		}
	}

	@Override
	public int getLineAdjustInterval() {
		return lineAdjustInterval;
	}

	@Override
	public void setLineAdjustInterval(int interval) {
		lineAdjustInterval = interval;
		if (explorer != null) {
			explorer.setLineAdjustInterval(interval);
		}
	}

	private class ExplorerListener extends AbstractStateListener<Explorer.ExplorerState> {

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
