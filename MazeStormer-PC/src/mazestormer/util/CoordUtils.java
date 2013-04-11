package mazestormer.util;

import java.awt.geom.Point2D;

import lejos.robotics.navigation.Pose;

public final class CoordUtils {

	private CoordUtils() {
	}

	/**
	 * Convert the given robot position to map coordinates.
	 * 
	 * @param robotPosition
	 *            The position in robot coordinates.
	 */
	public static <P extends Point2D> P toMapCoordinates(P robotPosition) {
		@SuppressWarnings("unchecked")
		P mapPosition = (P) robotPosition.clone();
		mapPosition.setLocation(robotPosition.getX(), -robotPosition.getY());
		return mapPosition;
	}

	/**
	 * Convert the given map position to robot coordinates.
	 * 
	 * @param mapPosition
	 *            The position in map coordinates.
	 */
	public static <P extends Point2D> P toRobotCoordinates(P mapPosition) {
		return toMapCoordinates(mapPosition);
	}

	/**
	 * Convert the given robot heading to map coordinates.
	 * 
	 * @param robotHeading
	 *            The heading in robot coordinates.
	 */
	public static float toMapCoordinates(float robotHeading) {
		return -robotHeading + 90f;
	}

	/**
	 * Convert the given map heading to robot coordinates.
	 * 
	 * @param mapHeading
	 *            The heading in map coordinates.
	 */
	public static float toRobotCoordinates(float mapHeading) {
		return toMapCoordinates(mapHeading);
	}

	/**
	 * Convert the given robot pose to map coordinates.
	 * 
	 * @param robotPose
	 *            The pose in robot coordinates.
	 */
	public static Pose toMapCoordinates(Pose robotPose) {
		Pose mapPose = new Pose();
		mapPose.setLocation(toMapCoordinates(robotPose.getLocation()));
		mapPose.setHeading(toMapCoordinates(robotPose.getHeading()));
		return mapPose;
	}

	/**
	 * Convert the given map pose to robot coordinates.
	 * 
	 * @param mapPose
	 *            The pose in map coordinates.
	 */
	public static Pose toRobotCoordinates(Pose mapPose) {
		return toMapCoordinates(mapPose);
	}

}
