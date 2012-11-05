package mazestormer.ui.map.event;

import lejos.robotics.navigation.Pose;

public class MapRobotPoseChangeEvent {

	private final Pose pose;

	public MapRobotPoseChangeEvent(Pose pose) {
		this.pose = pose;
	}

	public Pose getPose() {
		return pose;
	}

}
