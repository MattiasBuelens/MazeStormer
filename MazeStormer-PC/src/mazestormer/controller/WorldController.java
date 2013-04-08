package mazestormer.controller;

import mazestormer.world.World;

public class WorldController extends SubController implements IWorldController {

	private WorldMapController map;
	private WorldLogController log;

	public WorldController(MainController mainController, World world) {
		super(mainController);
		this.map = new WorldMapController(getMainController(), world);
		this.log = new WorldLogController(getMainController(), world);
	}

	@Override
	public IWorldMapController map() {
		return map;
	}

	@Override
	public ILogController log() {
		return log;
	}

}
