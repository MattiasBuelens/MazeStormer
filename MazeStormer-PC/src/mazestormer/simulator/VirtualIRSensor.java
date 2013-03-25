package mazestormer.simulator;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.player.AbsolutePlayer;
import mazestormer.robot.IRSensor;
import mazestormer.world.World;

public class VirtualIRSensor implements IRSensor {
	
	private World world;

	public VirtualIRSensor(World world) {
		this.world = world;
	}
	
	private World getWorld() {
		return this.world;
	}

	private PoseProvider getPoseProvider() {
		return getWorld().getLocalPlayer().getRobot().getPoseProvider();
	}

	private IMaze getMaze() {
		return getWorld().getLocalPlayer().getMaze();
	}

	@Override
	public float getAngle() {
		return getDetectedRobotAngle();
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}
	
	private float getDetectedRobotAngle() {
		
		// TODO: relativeBearing fails
		// TODO: heading vs x-axis?
		// TODO: edge == null?
		
		List<Float> detectedRobotAngles = new ArrayList<Float>();
		
		Pose currentPose = getPoseProvider().getPose();
		Tile currentTile = getTileAt(currentPose);
		
		// Check if IR scan is allowed in x or y direction.
		float currentAngle = currentPose.getHeading();
		long x = 0;
		long y = 0;
		if (-45 <= currentAngle && currentAngle <= 45) {
			x = 1;
		}
		else if (45 <= currentAngle && currentAngle <= 135) {
			y = 1;
		}
		else if (-135 <= currentAngle && currentAngle <= -45) {
			y = -1;
		}
		else {
			x = -1;
		}
		
		// ROBOT DETECTION
		for (AbsolutePlayer ap : getWorld().getPlayers()) {
			if (getWorld().getLocalPlayer() != ap) {
				Pose otherPose = ap.getRobot().getPoseProvider().getPose();
				Float angleDiff = currentPose.relativeBearing(otherPose.getLocation());
				if (-180 <= angleDiff && angleDiff <= 180) {
					Tile otherTile = getTileAt(otherPose);
					
					if (currentTile == otherTile) {
						detectedRobotAngles.add(angleDiff);
						break;
					}
					
					long diffX = currentTile.getX() - otherTile.getX();
					long diffY = currentTile.getY() - otherTile.getY();
					
					// Check if other robot is positioned in scan direction
					if (x == 0 && diffX != 0) {
						break;
					}	
					if (y == 0 && diffY != 0) {
						break;
					}
					
					// Check if other robot is positioned in marked area
					if (Math.abs(diffX) > DetectionLength.ROBOT.getTransX()) {
						break;
					}
					if (Math.abs(diffY) > DetectionLength.ROBOT.getTransY()) {
						break;
					}
					
					// Check if other robot is positioned in observable area
					// @note: tile creation possible, not really a problem
					boolean target = false;
					if (x != 0) {
						for(int i = 1; i < DetectionLength.ROBOT.getTransX() && !target; i++) {
							Tile tileToCheck = getMaze().getTileAt(new Point(currentTile.getX()+x*i, currentTile.getY()));
							if (x == 1) {
								if (tileToCheck.getEdgeAt(Orientation.WEST) == null) {
									break;
								}
							} else {
								if (tileToCheck.getEdgeAt(Orientation.EAST) == null) {
									break;
								}
							}
							target = (tileToCheck.getX()-otherTile.getX()==0);
						}
					} else {
						for(int i = 1; i < DetectionLength.ROBOT.getTransY() && !target; i++) {
							Tile tileToCheck = getMaze().getTileAt(new Point(currentTile.getX(), currentTile.getY()+y*i));
							if (y == 1) {
								if (tileToCheck.getEdgeAt(Orientation.SOUTH) == null) {
									break;
								}
							} else {
								if (tileToCheck.getEdgeAt(Orientation.NORTH) == null) {
									break;
								}
							}
							target = (tileToCheck.getY()-otherTile.getY()==0);
						}
					}
					
					if (target == true) {
						detectedRobotAngles.add(angleDiff);
					}
				}	
			}
		}
	
		if (detectedRobotAngles.size() != 0) {
			// Selecting the closest angle difference
			float bestAngle = 180;
			for (Float f : detectedRobotAngles) {
				if (Math.abs(bestAngle) > Math.abs(f)) {
					bestAngle = f;
				}
			}
			// Detected robot has higher priority than a seesaw
			return bestAngle;
		}
		
		// SEESAW DETECTION
		// @note: tile creation possible, not really a problem
		for(int i = 1; i < DetectionLength.SEESAW.getTransY(); i++) {
			Tile tileToCheck = getMaze().getTileAt(new Point(currentTile.getX(), currentTile.getY()+y*i));
			if (tileToCheck.isSeesaw() && !tileToCheck.isSeesawOpen()){
				return 0f;
			}
		}
		
		return Float.NaN;
	}
	
	private Tile getTileAt(Pose pose) {
		return getTileAt(pose.getLocation());
	}
	
	private Tile getTileAt(Point position) {
		// Get tile from absolute position
		Point relativePosition = getMaze().toRelative(position);
		Point tilePosition = getMaze().toTile(relativePosition);
		return getMaze().getTileAt(tilePosition);
	}
	
	public enum DetectionLength {
		
		ROBOT(1,3), SEESAW(0,2);
		
		private int transX;
		private int transY;

		private DetectionLength(int transX, int transY) {
			this.transX = transX;
			this.transY = transY;
		}
		
		public int getTransX() {
			return this.transX;
		}
		
		public int getTransY() {
			return this.transY;
		}
	}
	
	

}
