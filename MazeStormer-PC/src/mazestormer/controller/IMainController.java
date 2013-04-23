package mazestormer.controller;

import mazestormer.game.player.RelativePlayer;
import mazestormer.util.EventSource;
import mazestormer.world.World;

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

	public RelativePlayer getPlayer();

	public World getWorld();

}
