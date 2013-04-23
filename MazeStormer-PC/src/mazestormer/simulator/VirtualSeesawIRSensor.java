package mazestormer.simulator;

import mazestormer.infrared.IRBall;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class VirtualSeesawIRSensor extends WorldIRSensor {

	public VirtualSeesawIRSensor(World world) {
		super(world, IRBall.class, IRDetectionMode.VIRTUAL);
	}
}
