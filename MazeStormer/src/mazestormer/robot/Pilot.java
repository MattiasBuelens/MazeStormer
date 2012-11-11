package mazestormer.robot;

import lejos.robotics.navigation.RotateMoveController;

public interface Pilot extends RotateMoveController {

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
