package mazestormer.robot;

import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.MoveProvider;

public interface Pilot extends ArcRotateMoveController, MoveProvider {

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
