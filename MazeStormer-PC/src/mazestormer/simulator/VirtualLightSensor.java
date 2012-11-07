package mazestormer.simulator;

import java.awt.geom.Rectangle2D;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.LampLightDetector;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.Maze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.Robot;

public class VirtualLightSensor implements LampLightDetector {

	public static final int BROWN_VALUE = 410;
	public static final int WHITE_VALUE = 450;

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

	/**
	 * Not implemented.
	 */
	@Override
	public int getLightValue() {
		return 0;
	}

	// private void waitUntil(long when) {
	// long delay = when - System.currentTimeMillis();
	// Delay.msDelay(delay);
	// }

	// @Override
	// public int getNormalizedLightValue() {
	// int value = nextValue;
	// waitUntil(nextTime);
	// nextValue = readNormalizedLightValue();
	// nextTime = System.currentTimeMillis() + delay;
	// return value;
	// }

	// private int readNormalizedLightValue() {
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
			if (getSide(tile, orientation).contains(relativePosition)) {
				return WHITE_VALUE;
			}
		}

		return BROWN_VALUE;
	}

	private Rectangle2D getSide(Tile tile, Orientation orientation) {
		// Get edge points in tile coordinates
		Line line = orientation.getLine();
		Point tilePosition = tile.getPosition().toPoint();
		Point p1 = line.getP1().add(tilePosition);
		Point p2 = line.getP2().add(tilePosition);

		// Convert to relative coordinates
		p1 = getMaze().fromTile(p1);
		p2 = getMaze().fromTile(p2);

		// Shift points to account for edge size
		float halfLineThickness = getMaze().getEdgeSize() / 2f;
		Point shift = new Point(halfLineThickness, halfLineThickness);
		p1 = p1.subtract(shift);
		p2 = p2.add(shift);

		// Return bounding box
		return new Rectangle2D.Double(p1.getX(), p1.getY(), p2.getX()
				- p1.getX(), p2.getY() - p1.getY());
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getHigh() {
		return 0;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getLow() {
		return 0;
	}

	@Override
	public void setFloodlight(boolean floodlight) {
	}

	@Override
	public boolean isFloodlightOn() {
		return true;
	}

	@Override
	public int getFloodlight() {
		return 0;
	}

	@Override
	public boolean setFloodlight(int color) {
		return false;
	}

}
