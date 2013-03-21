package mazestormer.simulator.collision;

import java.util.HashSet;
import java.util.Set;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;

public class VirtualCollisionDetector {

	private final Maze maze;
	private final PoseProvider poseProvider;
	private static final float ROBOT_WIDTH = 8.0f;
	private static final float ROBOT_HEIGHT = 15.0f;

	public VirtualCollisionDetector(Maze maze, PoseProvider poseProvider) {
		this.maze = maze;
		this.poseProvider = poseProvider;
	}

	public boolean onWall() {
		Iterable<Point> robotCornerPositions = getRobotCornerPositions();
		for (Point cornerPosition : robotCornerPositions) {
			Point relativePosition = getMaze().toRelative(cornerPosition);
			// Get tile underneath the robot corner
			Tile tile = getTileUnder(relativePosition);
			// Check if the corner is on a wall
			if (onWall(relativePosition, tile)) {
				return true;
			}
		}
		return false;
	}

	public Maze getMaze() {
		return maze;
	}

	private Iterable<Point> getRobotCornerPositions() {
		Set<Point> corners = new HashSet<Point>();
		Pose pose = poseProvider.getPose();
		Point center = pose.getLocation();
		float heading = pose.getHeading();

		// The distance from the center of the robot to a corner.
		float distanceToCorners = (float) Math.sqrt(Math
				.pow(ROBOT_WIDTH / 2, 2) + Math.pow(ROBOT_HEIGHT / 2, 2));
		// The angle between the axle over the width, and the line to the front
		// right corner.
		float angle = (float) Math.atan2(ROBOT_HEIGHT / 2, ROBOT_WIDTH / 2);
		angle = (float) Math.toDegrees(angle);

		corners.add(center.pointAt(distanceToCorners, heading - 90 + angle));
		corners.add(center.pointAt(distanceToCorners, heading - 90 - angle));
		corners.add(center.pointAt(distanceToCorners, heading + 90 + angle));
		corners.add(center.pointAt(distanceToCorners, heading + 90 - angle));

		return corners;

	}

	private Tile getTileUnder(Point relativePosition) {
		Point tilePosition = getMaze().toTile(relativePosition);
		Tile tile = getMaze().getTileAt(tilePosition);
		return tile;
	}

	private boolean onWall(Point relativePosition, Tile tile) {
		for (Orientation orientation : tile.getClosedSides()) {
			if (getMaze().getEdgeBounds(tile.getEdgeAt(orientation)).contains(
					relativePosition)) {
				return true;
			}
		}
		return false;
	}
}
