package mazestormer.maze;

import java.awt.geom.Point2D;

import lejos.geom.Point;

import com.google.common.math.IntMath;

public enum Orientation {
	/**
	 * North, the positive Y orientation.
	 */
	NORTH("N", new Point(0, 1)),

	/**
	 * East, the positive X orientation.
	 */
	EAST("E", new Point(1, 0)),

	/**
	 * South, the negative Y orientation.
	 */
	SOUTH("S", new Point(0, -1)),

	/**
	 * West, the negative X orientation.
	 */
	WEST("W", new Point(-1, 0));

	private final String shortName;
	private final Point delta;

	private Orientation(String shortName, Point delta) {
		this.shortName = shortName;
		this.delta = delta;
	}

	private Orientation(String shortName, float dx, float dy) {
		this(shortName, new Point(dx, dy));
	}

	/**
	 * Get the short name of this orientation.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Get the delta vector of this orientation.
	 */
	public Point getDelta() {
		return delta;
	}

	/**
	 * Rotate this orientation clockwise for the given amount of times.
	 * 
	 * <p>Example: Orientation.NORTH.rotateClockwise(3) == Orientation.WEST
	 *  
	 * @param amount
	 * 			The amount of times to rotate.
	 * 
	 * @return The rotated orientation.
	 */
	public Orientation rotateClockwise(int amount) {
		Orientation[] values = Orientation.values();
		// Add ordinal to amount and normalize
		// Note: the modulo operation (mod) is needed
		// as opposed to the remainder operator (%)
		int nextOrdinal = IntMath.mod(this.ordinal() + amount, values.length);
		return Orientation.values()[nextOrdinal];
	}

	/**
	 * Rotate this orientation clockwise.
	 * 
	 * <p>Example: Orientation.NORTH.rotateClockwise() == Orientation.EAST
	 * 
	 * @return The rotated orientation.
	 */
	public Orientation rotateClockwise() {
		return rotateClockwise(1);
	}

	/**
	 * Rotate this orientation counter-clockwise for the given amount of times.
	 * 
	 * <p>Example: Orientation.EAST.rotateCounterClockwise(3) == Orientation.SOUTH
	 *  
	 * @param amount
	 * 			The amount of times to rotate.
	 * 
	 * @return The rotated orientation.
	 */
	public Orientation rotateCounterClockwise(int amount) {
		return rotateClockwise(-amount);
	}

	/**
	 * Rotate this orientation counter-clockwise.
	 * 
	 * <p>Example: Orientation.NORTH.rotateCounterClockwise() == Orientation.WEST
	 * 
	 * @return The rotated orientation.
	 */
	public Orientation rotateCounterClockwise() {
		return rotateCounterClockwise(1);
	}

	/**
	 * Shift the given point in the direction of this orientation by the given
	 * shift amount.
	 * 
	 * @param point
	 *            The point to shift.
	 * @param amount
	 *            The shift amount.
	 * 
	 * @return The shifted point.
	 */
	public <P extends Point2D> P shift(P point, float amount) {
		Point shift = getDelta().multiply(amount);
		double x = point.getX() + shift.getX();
		double y = point.getY() + shift.getY();
		@SuppressWarnings("unchecked")
		P result = (P) point.clone();
		result.setLocation(x, y);
		return result;
	}

	/**
	 * Shift the given point in the direction of this orientation by one unit.
	 * 
	 * @param point
	 *            The point to shift.
	 * 
	 * @return The shifted point. | result.equals(shift(point, 1))
	 */
	public <P extends Point2D> P shift(P point) {
		return shift(point, 1);
	}

	/**
	 * Get an orientation by its short name.
	 * 
	 * @param shortName
	 * 			The short name of the orientation.
	 * 
	 * @return The orientation, or null if not found.
	 */
	public static Orientation byShortName(String shortName) {
		if (shortName != null) {
			for (Orientation orientation : values()) {
				if (orientation.getShortName().equalsIgnoreCase(shortName)) {
					return orientation;
				}
			}
		}
		return null;
	}

}
