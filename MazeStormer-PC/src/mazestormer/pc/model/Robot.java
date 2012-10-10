package mazestormer.pc.model;

public interface Robot {

	/**
	 * Move the robot forward by the given distance.
	 * 
	 * @param distance
	 *            The distance in centimeters.
	 */
	public void moveForward(long distance);

	/**
	 * Move the robot backward by the given distance.
	 * 
	 * @param distance
	 *            The distance in centimeters.
	 */
	public void moveBackward(long distance);

	/**
	 * Turn the robot clockwise by the given angle.
	 * 
	 * @param angle
	 *            The angle in degrees.
	 */
	public void turnClockwise(float angle);

	/**
	 * Turn the robot counter-clockwise by the given angle.
	 * 
	 * @param angle
	 *            The angle in degrees.
	 */
	public void turnCounterClockwise(float angle);

}
