package mazestormer.simulator.collision;

import java.util.HashSet;
import java.util.Set;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;

public class VirtualCollisionDetector {

	private final Maze maze;
	private final PoseProvider poseProvider;
	private static final float ROBOT_WIDTH = 8.0f;
	private static final float ROBOT_LENGTH = 15.0f;
	
	public VirtualCollisionDetector(Maze maze, PoseProvider poseProvider) {
		this.maze = maze;
		this.poseProvider = poseProvider;
	}
	
	public boolean onWall() {
		boolean onWall = false;
		Iterable<Point> robotCornerPositions = getRobotCornerPositions();
		for(Point cornerPosition : robotCornerPositions) {
			Point relativePosition = getMaze().toRelative(cornerPosition);
			// get tile underneath the robot corner
			Tile tile = getTileUnder(relativePosition);
			// check if the corner is on a wall
			if(onWall(relativePosition, tile))
				onWall = true;
		}
		return onWall;
	}
	
	public Maze getMaze() {
		return maze;
	}
	
	private Iterable<Point> getRobotCornerPositions() {
		
		Set<Point> corners = new HashSet<Point>();
		Point center = poseProvider.getPose().getLocation();
		float heading = poseProvider.getPose().getHeading();
		
		float distanceToCorners = (float) Math.sqrt(Math.pow(ROBOT_WIDTH/2,2) +
				Math.pow(ROBOT_LENGTH/2,2));
		// the angle between the axle over the width, and the line to the front right corner.
		float angle = (float) Math.atan2(ROBOT_LENGTH/2, ROBOT_WIDTH/2);
		angle = (float) Math.toDegrees(angle);
		
		corners.add(center.pointAt(distanceToCorners, heading-90+angle));
		corners.add(center.pointAt(distanceToCorners, heading-90-angle));
		corners.add(center.pointAt(distanceToCorners, heading+90+angle));
		corners.add(center.pointAt(distanceToCorners, heading+90-angle));
		
		return corners;
		
	}
	
	private Tile getTileUnder(Point relativePosition) {
		Point tilePosition = getMaze().toTile(relativePosition);
		Tile tile = getMaze().getTileAt(tilePosition);
		return tile;
	}
	
	private boolean onWall(Point relativePosition, Tile tile) {
		for(Orientation orientation : tile.getClosedSides()) {
			if(tile.getSide(orientation, getMaze()).contains(relativePosition))
				return true;
		}
		return false;
	}
}
