package mazestormer.robot;

import mazestormer.infrared.IRRobot;

public interface ControllablePCRobot extends ControllableRobot, IRRobot {
	
	public static final int STANDARD_IR_RANGE = 45;

	public IRSensor getRobotIRSensor();

}
