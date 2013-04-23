package mazestormer.simulator;

import mazestormer.infrared.IRSource;
import mazestormer.robot.IRSensor;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public abstract class WorldIRSensor implements IRSensor {

	public static final float ANGLE = (float) Math.toDegrees(120);

	private final WorldIRDetector wird;
	private World world;

	protected WorldIRSensor(World world, Class<? extends IRSource> irDetectionType, IRDetectionMode mode) {
		this.world = world;
		this.wird = new WorldIRDetector(getWorld(), ANGLE, irDetectionType, mode);
	}

	private World getWorld() {
		return this.world;
	}

	private WorldIRDetector getWorldIRDetector() {
		return this.wird;
	}

	@Override
	public float getAngle() {
		return getWorldIRDetector().getAngle();
	}

	@Override
	public boolean hasReading() {
		return getWorldIRDetector().hasReading();
	}

}
