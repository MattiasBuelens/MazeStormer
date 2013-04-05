package mazestormer.simulator;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.IRSensor;
import mazestormer.world.World;

public class VirtualIRSensor implements IRSensor {
	
	private final WorldIRDetector wird;
	private World world;

	public VirtualIRSensor(World world) {
		this.world = world;
		//TODO: dimension support
		//TODO: mode needed
		this.wird = new WorldIRDetector(getWorld(), getPoseProvider(), 0, 0);
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
