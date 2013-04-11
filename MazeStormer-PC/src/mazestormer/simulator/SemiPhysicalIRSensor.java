package mazestormer.simulator;

import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class SemiPhysicalIRSensor extends WorldIRSensor {

	public SemiPhysicalIRSensor(World world) {
		super(world, IRDetectionMode.SEMI_PHYSICAL);
	}
}
