package mazestormer.simulator;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.IRSensor;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public abstract class WorldIRSensor implements IRSensor {
	
	private final WorldIRDetector wird;
	private World world;

	protected WorldIRSensor(World world, IRDetectionMode mode) {
		this.world = world;
		//TODO: values?
		this.wird = new WorldIRDetector(getWorld(), getPoseProvider(), 0, 0, mode);
	}

	private World getWorld() {
		return this.world;
	}

	private PoseProvider getPoseProvider() {
		return getWorld().getLocalPlayer().getRobot().getPoseProvider();
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
