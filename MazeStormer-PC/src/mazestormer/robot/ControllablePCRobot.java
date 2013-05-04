package mazestormer.robot;

import mazestormer.infrared.IRRobot;

public interface ControllablePCRobot extends ControllableRobot, IRRobot {

	public IRSensor getRobotIRSensor();

}
