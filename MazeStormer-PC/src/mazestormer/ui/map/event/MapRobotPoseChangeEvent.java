package mazestormer.ui.map.event;

import lejos.robotics.navigation.Pose;
import mazestormer.player.PlayerIdentifier;

public class MapRobotPoseChangeEvent extends MapEvent {

	private final Pose pose;

	public MapRobotPoseChangeEvent(Pose pose, PlayerIdentifier player) {
		super(player);
		this.pose = pose;
	}

	public Pose getPose() {
		return pose;
	}

}
