package mazestormer.util;

import lejos.robotics.navigation.Pose;

public class MapUtils {

	private MapUtils() {
	}

	/**
	 * Convert the given pose to map coordinates.
	 * 
	 * @param pose
	 * 			The pose in robot coordinates.
	 */
	public static Pose toMapCoordinates(Pose pose) {
		return new Pose(pose.getX(), -pose.getY(), -pose.getHeading() + 90f);
	}

}
