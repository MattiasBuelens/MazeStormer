package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IMainController {

	public IConfigurationController configuration();
	
	public ICalibrationController calibration();

	public IParametersController parameters();

	public IManualControlController manualControl();

	public IPolygonControlController polygonControl();

	public IBarcodeController barcodeControl();
	
	public IPathFindingController pathFindingControl();

	public ILineFinderController lineFinderControl();
	
	public ICheatController cheatControl();

	public IMapController map();

	public ILogController log();

	public IStateController state();
	
	public IGameController gameControl();

	public void register(EventSource eventSource);

	IExplorerController explorerControl();

}
