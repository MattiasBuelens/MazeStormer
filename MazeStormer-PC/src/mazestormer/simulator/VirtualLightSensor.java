package mazestormer.simulator;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.AbstractCalibratedLightSensor;
import mazestormer.robot.Robot;

public class VirtualLightSensor extends AbstractCalibratedLightSensor {

	public static final int BROWN_VALUE = 512;
	public static final int WHITE_VALUE = 582;

	private Maze maze;
	private PoseProvider poseProvider;

	// private int delay = 50; // ms
	// private long nextTime = 0;
	// private int nextValue = 0;

	public VirtualLightSensor(Maze maze, PoseProvider poseProvider) {
		this.maze = maze;
		this.poseProvider = poseProvider;
	}

	private Maze getMaze() {
		return maze;
	}

	private PoseProvider getPoseProvider() {
		return poseProvider;
	}

	@Override
	public int getNormalizedLightValue() {
		// Get absolute robot pose
		Pose pose = getPoseProvider().getPose();
		// Add sensor offset
		Point position = pose.getLocation().pointAt(Robot.sensorOffset,
				pose.getHeading());

		// Get tile underneath robot
		Point relativePosition = getMaze().toRelative(position);
		Point tilePosition = getMaze().toTile(relativePosition);
		Tile tile = getMaze().getTileAt(tilePosition);

		// Check if robot is on open side of tile
		for (Orientation orientation : tile.getOpenSides()) {
			if (tile.getSide(orientation, getMaze()).contains(relativePosition)) {
				return WHITE_VALUE;
			}
		}

		return BROWN_VALUE;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void setFloodlight(boolean floodlight) {
	}

	/**
	 * Not implemented.
	 */
	@Override
	public boolean isFloodlightOn() {
		return true;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getFloodlight() {
		return 0;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public boolean setFloodlight(int color) {
		return false;
	}

	@Override
	public float getSensorRadius() {
		return 0;
	}

}
