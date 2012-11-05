package mazestormer.util;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public class MapUtils {

	private MapUtils() {
	}

	/**
	 * Convert the given position to map coordinates.
	 * 
	 * @param position
	 *            The position in robot coordinates.
	 */
	public static Point2D toMapCoordinates(Point2D position) {
		return new Point2D.Double(position.getX(), -position.getY());
	}

	/**
	 * Convert the given pose to map coordinates.
	 * 
	 * @param pose
	 *            The pose in robot coordinates.
	 */
	public static Pose toMapCoordinates(Pose pose) {
		return new Pose(pose.getX(), -pose.getY(), -pose.getHeading() + 90f);
	}

}
