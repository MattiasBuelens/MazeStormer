package mazestormer.simulator.collision;

import java.util.HashSet;
import java.util.Set;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.Robot;
import mazestormer.world.World;

public class VirtualCollisionDetector {

	private final World world;

	public VirtualCollisionDetector(World world) {
		this.world = world;
	}

	private World getWorld() {
		return this.world;
	}

	private Robot getRobot() {
		return getWorld().getLocalPlayer().getRobot();
	}

	private PoseProvider getPoseProvider() {
		return getRobot().getPoseProvider();
	}

	private IMaze getMaze() {
		return getWorld().getMaze();
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

	private Iterable<Point> getRobotCornerPositions() {
		Set<Point> corners = new HashSet<Point>();
		Pose pose = getPoseProvider().getPose();
		Point center = pose.getLocation();
		float heading = pose.getHeading();

		// The distance from the center of the robot to a corner.
		float distanceToCorners = (float) Math.sqrt(Math.pow(getRobot().getWidth() / 2, 2)
				+ Math.pow(getRobot().getHeight() / 2, 2));
		// The angle between the axle over the width, and the line to the front
		// right corner.
		float angle = (float) Math.atan2(getRobot().getHeight() / 2, getRobot().getWidth() / 2);
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
			if (getMaze().getEdgeBounds(tile.getEdgeAt(orientation)).contains(relativePosition)) {
				return true;
			}
		}
		return false;
	}
}
