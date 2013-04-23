package mazestormer.simulator;

import mazestormer.infrared.IRRobot;
import mazestormer.simulator.WorldIRDetector.IRDetectionMode;
import mazestormer.world.World;

public class VirtualRobotIRSensor extends WorldIRSensor {

	public VirtualRobotIRSensor(World world) {
		super(world, IRRobot.class, IRDetectionMode.VIRTUAL);
	}

}
