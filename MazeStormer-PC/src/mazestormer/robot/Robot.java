package mazestormer.robot;

import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.MoveController;
import lejos.robotics.navigation.MoveProvider;

public interface Robot extends ArcRotateMoveController, MoveProvider {

	/**
	 * Left wheel diameter, in centimeters.
	 */
	public final static double leftWheelDiameter = 3.0;//MoveController.WHEEL_SIZE_NXT1;

	/**
	 * Right wheel diameter, in centimeters.
	 */
	public final static double rightWheelDiameter = 3.01;//1.0045 * MoveController.WHEEL_SIZE_NXT1;

	/**
	 * Distance between center of wheels, in centimeters.
	 */
	public final static double trackWidth = 13.97d;//16.1d;

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
