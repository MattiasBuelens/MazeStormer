package mazestormer.controller;

import java.util.logging.Logger;

import mazestormer.world.World;

public class WorldLogController extends LogController {

	private final World world;

	public WorldLogController(MainController mainController, World world) {
		super(mainController);
		this.world = world;
	}

	private World getWorld() {
		return world;
	}

	@Override
	protected Logger getLogger() {
		return getWorld().getLogger();
	}

}
