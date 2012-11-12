package mazestormer.robot;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import mazestormer.detect.RangeScannerFeatureDetector;

public interface Robot {

	/**
	 * Left wheel diameter, in centimeters.
	 */
	public final static float leftWheelDiameter = 3.0f;

	/**
	 * Right wheel diameter, in centimeters.
	 */
	public final static float rightWheelDiameter = 3.01f;

	/**
	 * Distance between center of wheels, in centimeters.
	 */
	public final static float trackWidth = 14.3f;// 13.97f;

	/**
	 * Distance between light sensor and center of wheel axis, in centimeters.
	 */
	public final static float sensorOffset = 7.2f;

	/**
	 * Get the pilot controlling this robot's movement.
	 */
	public Pilot getPilot();

	/**
	 * Get the calibrated light sensor of this robot.
	 */
	public CalibratedLightSensor getLightSensor();

	/**
	 * Get the range scanner of this robot.
	 */
	public RangeScanner getRangeScanner();

	/**
	 * Get the range feature detector of this robot.
	 */
	public RangeScannerFeatureDetector getRangeDetector();

	/**
	 * Get the pose provider of this robot.
	 */
	public PoseProvider getPoseProvider();

	/**
	 * Terminate this robot.
	 */
	public void terminate();

}
