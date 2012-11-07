package mazestormer.robot;

import lejos.robotics.navigation.ArcRotateMoveController;

public interface Pilot extends ArcRotateMoveController{

	/**
	 * Left wheel diameter, in centimeters.
	 */
	public final static double leftWheelDiameter = 3.0;

	/**
	 * Right wheel diameter, in centimeters.
	 */
	public final static double rightWheelDiameter = 3.01;

	/**
	 * Distance between center of wheels, in centimeters.
	 */
	public final static double trackWidth = 13.97d;

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
