package mazestormer.simulator;

import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.IMaze;
import mazestormer.robot.IRSensor;
import mazestormer.world.World;

public class VirtualIRSensor implements IRSensor {
	
	private World world;

	public VirtualIRSensor(World world) {
		this.world = world;
	}
	
	private World getWorld() {
		return this.world;
	}

	private PoseProvider getPoseProvider() {
		return getWorld().getLocalPlayer().getRobot().getPoseProvider();
	}

	private IMaze getMaze() {
		return getWorld().getLocalPlayer().getMaze();
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
