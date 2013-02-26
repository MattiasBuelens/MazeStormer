package mazestormer.ui.map.event;

import lejos.robotics.navigation.Pose;

public class MapRobotPoseChangeEvent extends MapEvent {

	private final Pose pose;

	public MapRobotPoseChangeEvent(Pose pose, String playerID) {
		super(playerID);
		this.pose = pose;
	}

	public Pose getPose() {
		return pose;
	}

}
