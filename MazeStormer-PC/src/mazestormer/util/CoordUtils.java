package mazestormer.util;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public class CoordUtils {

	private CoordUtils() {
	}

	/**
	 * Convert the given robot position to map coordinates.
	 * 
	 * @param position
	 *            The position in robot coordinates.
	 */
	public static Point2D toMapCoordinates(Point2D position) {
		return new Point2D.Double(position.getX(), -position.getY());
	}

	/**
	 * Convert the given map position to robot coordinates.
	 * 
	 * @param position
	 *            The position in map coordinates.
	 */
	public static Point2D toRobotCoordinates(Point2D position) {
		return toMapCoordinates(position);
	}

	/**
	 * Convert the given robot pose to map coordinates.
	 * 
	 * @param pose
	 *            The pose in robot coordinates.
	 */
	public static Pose toMapCoordinates(Pose pose) {
		return new Pose(pose.getX(), -pose.getY(), -pose.getHeading() + 90f);
	}

	/**
	 * Convert the given map pose to robot coordinates.
	 * 
	 * @param pose
	 *            The pose in map coordinates.
	 */
	public static Pose toRobotCoordinates(Pose pose) {
		return toMapCoordinates(pose);
	}

}
