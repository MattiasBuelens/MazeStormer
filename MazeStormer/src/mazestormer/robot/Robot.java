package mazestormer.robot;

import lejos.robotics.localization.PoseProvider;

public interface Robot {
	
	/**
	 * Get the pose provider of this robot.
	 */
	public PoseProvider getPoseProvider();

}
