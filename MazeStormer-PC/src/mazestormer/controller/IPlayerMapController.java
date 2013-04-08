package mazestormer.controller;

import lejos.robotics.navigation.Pose;

public interface IPlayerMapController extends IMapController {

	/**
	 * Get the robot's current pose, in map coordinates.
	 */
	public Pose getRobotPose();

	/**
	 * Clear the detected points on the map.
	 */
	public void clearRanges();

}
