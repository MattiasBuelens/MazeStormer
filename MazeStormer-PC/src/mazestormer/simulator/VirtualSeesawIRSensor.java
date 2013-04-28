package mazestormer.simulator;

import mazestormer.infrared.IRSeesaw;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class VirtualSeesawIRSensor extends WorldIRSensor {

	public VirtualSeesawIRSensor(World world) {
		super(world, IRSeesaw.class, IRDetectionMode.VIRTUAL);
	}

}
