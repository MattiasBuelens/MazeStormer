package mazestormer.robot;

import lejos.robotics.LampLightDetector;

public interface CalibratedLightSensor extends LampLightDetector {

	/**
	 * Get the normalized value corresponding to readValue() = 0%
	 */
	public int getLow();

	/**
	 * Get the normalized value corresponding to readValue() = 100%
	 */
	public int getHigh();

	/**
	 * Set the normalized value corresponding to readValue() = 0%
	 * 
	 * @param low
	 *            The normalized low value.
	 */
	public void setLow(int low);

	/**
	 * Set the normalized value corresponding to readValue() = 100%
	 * 
	 * @param high
	 *            The normalized high value.
	 */
	public void setHigh(int high);

	/**
	 * Calibrate the low value (0%) using the current light reading.
	 */
	public void calibrateLow();

	/**
	 * Calibrate the high value (100%) using the current light reading.
	 */
	public void calibrateHigh();

	/**
	 * Get the normalized light value from the given calibrated light value.
	 */
	public int getNormalizedLightValue(int lightValue);

	/**
	 * Get the calibrated light value from the given normalized light value.
	 */
	public int getLightValue(int normalizedLightValue);

	/**
	 * Get the approximate radius of the light sensor beam.
	 */
	public float getSensorRadius();

}
