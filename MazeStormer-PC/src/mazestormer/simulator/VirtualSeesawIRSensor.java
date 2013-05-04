package mazestormer.simulator;

import mazestormer.infrared.IRRobot;
import mazestormer.infrared.IRSeesaw;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class VirtualSeesawIRSensor extends WorldIRSensor {

	public VirtualSeesawIRSensor(World world) {
		super(world, IRRobot.SEESAW_IR_RANGE, IRSeesaw.class, IRDetectionMode.VIRTUAL);
	}

}
