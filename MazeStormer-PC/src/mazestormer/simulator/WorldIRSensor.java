package mazestormer.simulator;

import mazestormer.infrared.IRSource;
import mazestormer.robot.IRSensor;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public abstract class WorldIRSensor implements IRSensor {

	private final WorldIRDetector wird;
	private World world;

	protected WorldIRSensor(World world, float range, Class<? extends IRSource> irDetectionType, IRDetectionMode mode) {
		this.world = world;
		this.wird = new WorldIRDetector(getWorld(), range, irDetectionType, mode);
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
