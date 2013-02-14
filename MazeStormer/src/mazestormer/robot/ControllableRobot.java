package mazestormer.robot;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.ConditionalCommandBuilder;
import mazestormer.detect.RangeFeatureDetector;

public interface ControllableRobot extends ConditionalCommandBuilder, Robot {

	/**
	 * Left wheel diameter, in centimeters.
	 */
	public final static float leftWheelDiameter = 3.0f;

	/**
	 * Right wheel diameter, in centimeters.
	 */
	public final static float rightWheelDiameter = 2.999f;

	/**
	 * Distance between center of wheels, in centimeters.
	 */
	public final static float trackWidth = 14.25f;// 14.3f;

	/**
	 * Distance between light sensor and center of wheel axis, in centimeters.
	 */
	// public final static float sensorOffset = 7.2f;
	public final static float sensorOffset = 7.1f;

	/**
	 * Approximate radius of the light sensor beam.
	 */
	public final static float sensorRadius = 1.5f;

	/**
	 * Relative offset of the ultrasonic sensor from the center of the robot, in
	 * centimeters.
	 */
	// public static final Point sensorPosition = new Point(-3.4f, -0.6f);
	public static final Point sensorPosition = new Point(-0.8f, 0f);

	/**
	 * Gear ratio of the ultrasonic sensor head motor.
	 */
	public static final float sensorGearRatio = -56f / 40f;

	/**
	 * Maximum distance for ultasonic sensor range readings.
	 */
	public static final float sensorMaxDistance = 28;

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
	//public RangeScanner getRangeScanner();

	/**
	 * Get the range feature detector of this robot.
	 */
	public RangeFeatureDetector getRangeDetector();

	/**
	 * Get the sound player of this robot.
	 */
	public SoundPlayer getSoundPlayer();

	/**
	 * Terminate this robot.
	 */
	public void terminate();

}
