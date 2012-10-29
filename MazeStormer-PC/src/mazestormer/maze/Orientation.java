package mazestormer.maze;

import java.awt.geom.Point2D;

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
}
