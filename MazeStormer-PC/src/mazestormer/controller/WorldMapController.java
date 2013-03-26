package mazestormer.controller;

import mazestormer.player.Player;
import mazestormer.world.World;
import mazestormer.world.WorldListener;

public class WorldMapController extends MapController implements IWorldMapController {

	public WorldMapController(MainController mainController, World world) {
		super(mainController);
		world.addListener(new Listener());

		// Source maze
		addMaze(mainController.getWorld().getMaze(), "Source maze", 1);
	}

	private class Listener implements WorldListener {

		@Override
		public void playerAdded(Player player) {
			addPlayer(player);
		}

		@Override
		public void playerRemoved(Player player) {
			removePlayer(player);
		}

		@Override
		public void playerRenamed(Player player) {
			// Ignore
		}

	}

}
