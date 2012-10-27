package mazestormer.maze;

import lejos.geom.Point;

public enum Orientation {
	NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0);

	private final Point delta;

	private Orientation(Point delta) {
		this.delta = delta;
	}

	private Orientation(float dx, float dy) {
		this(new Point(dx, dy));
	}

	/**
	 * Get the delta vector of this orientation.
	 */
	public Point getDelta() {
		return delta;
	}

	/**
	 * Shift the given point in the direction of this orientation
	 * by the given shift amount.
	 * 
	 * @param point
	 * 			The point to shift.
	 * @param amount
	 * 			The shift amount.
	 * 
	 * @return The shifted point.
	 */
	public Point shift(Point point, float amount) {
		return getDelta().multiply(amount).addWith(point);
	}

	/**
	 * Shift the given point in the direction of this orientation
	 * by one unit.
	 * 
	 * @param point
	 * 			The point to shift.
	 * 
	 * @return The shifted point.
	 * 			| result.equals(shift(point, 1))
	 */
	public Point shift(Point point) {
		return shift(point, 1);
	}
}
