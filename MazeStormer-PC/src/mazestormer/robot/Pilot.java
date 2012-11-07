package mazestormer.robot;

import lejos.robotics.navigation.ArcRotateMoveController;

public interface Pilot extends ArcRotateMoveController{

	/**
	 * Starts the robot turning left (counter-clockwise).
	 */
	public void rotateLeft();

	/**
	 * Starts the robot turning right (clockwise).
	 */
	public void rotateRight();

	/**
	 * Stop and terminate this pilot.
	 */
	public void terminate();

}
