package mazestormer.simulator;

import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class VirtualIRSensor extends WorldIRSensor {

	public VirtualIRSensor(World world) {
		super(world, IRDetectionMode.VIRTUAL);
	}

}
