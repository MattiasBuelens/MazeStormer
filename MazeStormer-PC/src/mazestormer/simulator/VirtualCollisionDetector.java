package mazestormer.simulator;

import java.util.HashSet;
import java.util.Set;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;

public class VirtualCollisionDetector {

	private Detector detect;
	private boolean onWall = false;
	
	public VirtualCollisionDetector(Maze maze, PoseProvider poseProvider) {
		detect = new Detector(maze, poseProvider);
		detect.start();
	}
	
	public boolean onWall() {
		return onWall;
	}
	
	public void stopDetecting() {
		detect.stop();
	}
	
	private class Detector implements Runnable {
	
		private final Maze maze;
		private final PoseProvider poseProvider;
		private boolean isRunning = false;
		private static final float ROBOT_WIDTH = 14.0f;
		private static final float ROBOT_LENGTH = 17.0f;
		
		public Detector(Maze maze, PoseProvider poseProvider) {
			this.maze = maze;
			this.poseProvider = poseProvider;
		}
		
		public synchronized boolean isRunning() {
			return isRunning;
		}
		
		public void start() {
			isRunning = true;
			new Thread(this).start();
		}
		
		public void stop() {
			if(isRunning())
				isRunning = false;
		}
		
		public Maze getMaze() {
			return maze;
		}

		public void run() {
			while(isRunning()) {
				Iterable<Point> robotCornerPositions = getRobotCornerPositions();
				boolean tempOnWall = false; 
				for(Point cornerPosition : robotCornerPositions) {
					Point relativePosition = getMaze().toRelative(cornerPosition);
					// get tile underneath the robot corner
					Tile tile = getTileUnder(relativePosition);
					// check if the corner is on a wall
					if(onWall(relativePosition, tile))
						tempOnWall = true;
				}
				onWall = tempOnWall;
			}
		}
		
		private Iterable<Point> getRobotCornerPositions() {
			
			Set<Point> corners = new HashSet<Point>();
			Point center = poseProvider.getPose().getLocation();
			float heading = poseProvider.getPose().getHeading();
			
			float distanceToCorners = (float) Math.sqrt(Math.pow(ROBOT_WIDTH/2,2) +
					Math.pow(ROBOT_LENGTH/2,2));
			// the angle between the axle over the width, and the line to the front right corner.
			float angle = (float) Math.atan2(ROBOT_WIDTH/2, ROBOT_LENGTH/2);
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
}
