package mazestormer.simulator;

import mazestormer.robot.IRSensor;
import mazestormer.world.World;

public class VirtualIRSensor implements IRSensor {
	
	private World world;

	public VirtualIRSensor(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return this.world;
	}

	@Override
	public float getAngle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasReading() {
		// TODO Auto-generated method stub
		return false;
	}

}
