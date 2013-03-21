package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IExplorerController extends EventSource {

	public void startExploring();

	public void stopExploring();

	public boolean isLineAdjustEnabled();

	public void setLineAdjustEnabled(boolean isEnabled);

	public int getLineAdjustInterval();

	public void setLineAdjustInterval(int interval);

	public IBarcodeController getBarcodeController();

	public IParametersController getParametersController();

	public ICheatController getCheatController();

}
