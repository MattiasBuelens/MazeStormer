package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IMainController {

	public IConfigurationController configuration();

	public IParametersController parameters();

	public IManualControlController manualControl();

	public IPolygonControlController polygonControl();

	public IBarcodeController barcodeControl();

	public ILineFinderController lineFinderControl();

	public IMapController map();

	public ILogController log();

	public IStateController state();

	public void register(EventSource eventSource);

}
