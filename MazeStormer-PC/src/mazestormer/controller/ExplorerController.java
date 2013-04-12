package mazestormer.controller;

import mazestormer.command.Explorer;
import mazestormer.controlMode.Driver;
import mazestormer.player.CommandTools;
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

	private CommandTools getPlayer() {
		return getMainController().getPlayer();
	}

	private void postState(ExplorerEvent.EventType eventType) {
		postEvent(new ExplorerEvent(eventType));
	}

	@Override
	public void startExploring() {
		explorer = new Explorer(getPlayer());
		// Set parameters
		explorer.getDriver().setLineAdjustEnabled(isLineAdjustEnabled());
		explorer.getDriver().setLineAdjustInterval(getLineAdjustInterval());
		explorer.getDriver().setScanSpeed(getBarcodeController().getScanSpeed());
		// Start
		explorer.getDriver().addStateListener(new ExplorerListener());
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
			explorer.getDriver().setLineAdjustEnabled(isEnabled);
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
			explorer.getDriver().setLineAdjustInterval(interval);
		}
	}

	private class ExplorerListener extends AbstractStateListener<Driver.ExplorerState> {

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
