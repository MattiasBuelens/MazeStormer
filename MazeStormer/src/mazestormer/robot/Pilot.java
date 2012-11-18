package mazestormer.robot;

import lejos.robotics.navigation.RotateMoveController;

public interface Pilot extends RotateMoveController {

	/**
	 * The frequency of movement reports, in milliseconds.
	 */
	public static final long movementReportFrequency = 100;

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

	/**
	 * Sets the normal acceleration of the pilot in distance/second/second where
	 * distance is in the units of wheel diameter. The default value is 4 times
	 * the maximum travel speed.
	 * 
	 * @param acceleration
	 *            The new acceleration.
	 */
	public void setAcceleration(int acceleration);

}
