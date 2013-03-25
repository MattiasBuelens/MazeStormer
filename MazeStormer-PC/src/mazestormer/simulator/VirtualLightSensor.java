package mazestormer.simulator;

import java.awt.geom.Rectangle2D;

import lejos.geom.Point;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.maze.IMaze;
import mazestormer.maze.Orientation;
import mazestormer.maze.Tile;
import mazestormer.robot.AbstractCalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.world.World;

public class VirtualLightSensor extends AbstractCalibratedLightSensor {

	public static final int WHITE_VALUE = 580; // 100%
	public static final int BROWN_VALUE = 510; // 68%
	public static final int BLACK_VALUE = 360; // 0%

	private World world;

	public VirtualLightSensor(World world) {
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
	public int getNormalizedLightValue() {
		// Get absolute robot pose
		Pose pose = getPoseProvider().getPose();
		// Add sensor offset
		Point position = pose.getLocation().pointAt(ControllableRobot.sensorOffset,
				pose.getHeading());

		// Get tile underneath robot
		Point relativePosition = getMaze().toRelative(position);
		Point tilePosition = getMaze().toTile(relativePosition);
		Tile tile = getMaze().getTileAt(tilePosition);

		// Check if robot is on open side of tile
		for (Orientation orientation : tile.getOpenSides()) {
			if (getMaze().getEdgeBounds(tile.getEdgeAt(orientation)).contains(
					relativePosition)) {
				// On line
				return WHITE_VALUE;
			}
		}

		// Check if robot is on bar of barcode
		if (tile.hasBarcode()) {
			boolean isBlack = true;
			// Get the position of the robot relative to the corner of the tile
			Point relativeTilePosition = tilePosition.subtract(tile
					.getPosition().toPoint());
			for (Rectangle2D bar : getMaze().getBarcodeBars(tile)) {
				if (bar.contains(relativeTilePosition)) {
					// On bar
					return isBlack ? BLACK_VALUE : WHITE_VALUE;
				}
				isBlack = !isBlack;
			}
		}

		// On tile
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
