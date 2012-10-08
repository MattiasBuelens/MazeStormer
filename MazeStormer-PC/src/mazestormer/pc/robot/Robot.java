package mazestormer.pc.robot;

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

}
