package mazestormer.ui.map.event;

import lejos.robotics.navigation.Pose;
import mazestormer.controller.IMapController;
import mazestormer.game.player.PlayerIdentifier;

public class MapRobotPoseChangeEvent extends MapEvent {

	private final PlayerIdentifier player;
	private final Pose pose;

	public MapRobotPoseChangeEvent(IMapController owner, PlayerIdentifier player, Pose pose) {
		super(owner);
		this.player = player;
		this.pose = pose;
	}

	public PlayerIdentifier getPlayer() {
		return player;
	}

	public Pose getPose() {
		return pose;
	}

}
