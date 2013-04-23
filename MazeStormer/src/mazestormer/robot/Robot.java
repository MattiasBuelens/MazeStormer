package mazestormer.robot;

import lejos.robotics.localization.PoseProvider;

public interface Robot {

	/**
	 * Get the pose provider of this robot.
	 */
	public PoseProvider getPoseProvider();

	/**
	 * Get the width of this robot.
	 */
	public double getWidth();

	/**
	 * Get the height of this robot.
	 */
	public double getHeight();

}
