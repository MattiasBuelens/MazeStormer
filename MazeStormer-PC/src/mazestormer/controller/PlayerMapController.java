package mazestormer.controller;

import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.player.Player;
import mazestormer.ui.map.MazeLayer;
import mazestormer.ui.map.RangesLayer;

import com.google.common.eventbus.Subscribe;

public class PlayerMapController extends MapController implements IPlayerMapController {

	private final Player player;

	private RangesLayer rangesLayer;

	public PlayerMapController(MainController mainController, Player player) {
		super(mainController);

		this.player = player;

		addPlayer(player);

		// Source maze
		if (mainController.getPlayer().getPlayerID().equals(player.getPlayerID())) {
			MazeLayer sourceLayer = addMaze(mainController.getWorld().getMaze(), "Source maze", 1);
			sourceLayer.setZIndex(1);
			sourceLayer.setOpacity(0.5f);
		}

		// Discovered maze
		MazeLayer mazeLayer = addMaze(player.getMaze(), "Discovered maze", 2);

		// Ranges
		rangesLayer = new RangesLayer("Detected ranges");
		addLayer(rangesLayer);
		
		resetMap();
	}

	private Player getPlayer() {
		return player;
	}

	@Override
	public Pose getRobotPose() {
		return getMapPose(player);
	}

	@Override
	public void clearRanges() {
		rangesLayer.clear();
	}

	@Subscribe
	public void clearMazeOnConnect(ConnectEvent e) {
		if (e.isConnected()) {
			// Clear detected maze
			getPlayer().getMaze().clear();
			// Clear detected ranges
			clearRanges();
		}
	}

	public void terminate() {
		// Nothing to do here
	}

}
