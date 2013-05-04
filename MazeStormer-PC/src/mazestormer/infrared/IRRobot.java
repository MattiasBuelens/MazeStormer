package mazestormer.infrared;

import mazestormer.robot.Robot;

public interface IRRobot extends Robot, IRSource {

	public static final double DETECTION_RADIUS = 50.0; // in cm

	public static final int ROBOT_IR_RANGE = 45; // in degrees

	public static final int SEESAW_IR_RANGE = 45; // in degrees

}
