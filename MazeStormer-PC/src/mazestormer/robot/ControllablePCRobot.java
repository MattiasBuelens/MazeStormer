package mazestormer.robot;

import mazestormer.detect.ObservableRangeScanner;
import mazestormer.infrared.IRRobot;

public interface ControllablePCRobot extends ControllableRobot, IRRobot {

	/**
	 * Get the observable range scanner of this robot.
	 */
	@Override
	public ObservableRangeScanner getRangeScanner();

	/**
	 * Get the robot infrared sensor of this robot.
	 */
	public IRSensor getRobotIRSensor();

	/**
	 * Get the sound player of this robot.
	 */
	public SoundPlayer getSoundPlayer();

}
