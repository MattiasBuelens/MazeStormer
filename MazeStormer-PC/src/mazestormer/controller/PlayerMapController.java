package mazestormer.controller;

import lejos.robotics.navigation.Pose;
import mazestormer.connect.ConnectEvent;
import mazestormer.detect.RangeFeatureDetectEvent;
import mazestormer.player.Player;
import mazestormer.ui.map.RangesLayer;

import com.google.common.eventbus.Subscribe;

public class PlayerMapController extends MapController implements IPlayerMapController {

	private final Player player;
	private final RangesLayer rangesLayer;

	public PlayerMapController(MainController mainController, Player player) {
		super(mainController);

		// Player
		this.player = player;
		addPlayer(player);

		// Discovered maze
		addMaze(player.getMaze(), "Discovered maze", 2);

		// Ranges
		rangesLayer = new RangesLayer("Detected ranges");
		addLayer(rangesLayer);
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

	@Subscribe
	public void rangeFeatureDetected(RangeFeatureDetectEvent e) {
		rangesLayer.addRangeFeature(e.getFeature());
	}

	public void terminate() {
		// Nothing to do here
	}

}
