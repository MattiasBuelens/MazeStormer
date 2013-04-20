package mazestormer.controller;

import mazestormer.util.EventSource;

public interface IMainController extends EventSource {

	public IConfigurationController configuration();

	public ICalibrationController calibration();

	public IParametersController parameters();

	public IManualControlController manualControl();

	public IPolygonControlController polygonControl();

	public IBarcodeController barcodeControl();

	public IPathFindingController pathFindingControl();

	public ILineFinderController lineFinderControl();

	public ICheatController cheatControl();

	public IPlayerMapController map();

	public ILogController log();

	public IStateController state();

	public IGameController gameControl();

	public IGameSetUpController gameSetUpControl();

	public IExplorerController explorerControl();

}
