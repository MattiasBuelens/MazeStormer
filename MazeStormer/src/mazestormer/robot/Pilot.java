package mazestormer.robot;

import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.RotateMoveController;
import mazestormer.util.Future;

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
	 * Travel the given distance completely.
	 * 
	 * @param distance
	 *            The distance to travel.
	 * 
	 * @return A future which produces whether the travel was successfully
	 *         completed.
	 */
	public Future<Boolean> travelComplete(double distance);

	/**
	 * Rotate the given angle completely.
	 * 
	 * @param distance
	 *            The angle to rotate.
	 * 
	 * @return A future which produces whether the rotation was successfully
	 *         completed.
	 */
	public Future<Boolean> rotateComplete(double angle);

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

	/**
	 * Removes a registered move listener.
	 * 
	 * @param listener
	 *            The move listener.
	 */
	public void removeMoveListener(MoveListener listener);

}
