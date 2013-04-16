package mazestormer.infrared;

import mazestormer.robot.Robot;

public interface IRRobot extends Robot, IRSource {

	// in cm
	public static final double EXTERNAL_ZONE = 5.0;
	public static final double BRONS_HEIGHT = 21.0;
	public static final double BRONS_WIDTH = 9.0;
}
