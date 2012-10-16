package mazestormer.robot;

import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.MoveController;

public interface Robot extends ArcRotateMoveController {

	/**
	 * Left wheel diameter, in centimeters.
	 */
	public final static double leftWheelDiameter = MoveController.WHEEL_SIZE_NXT1;

	/**
	 * Right wheel diameter, in centimeters.
	 */
	public final static double rightWheelDiameter = 1.0045 * MoveController.WHEEL_SIZE_NXT1;

	/**
	 * Distance between center of wheels, in centimeters.
	 */
	public final static double trackWidth = 16.1d;

	/**
	 * Connect to this robot.
	 */
	public void connect();

	/**
	 * Disconnect from this robot.
	 */
	public void disconnect();

	/**
	 * Starts the NXT robot turning left (counter-clockwise).
	 */
	public void rotateLeft();

	/**
	 * Starts the NXT robot turning right (clockwise).
	 */
	public void rotateRight();

}
