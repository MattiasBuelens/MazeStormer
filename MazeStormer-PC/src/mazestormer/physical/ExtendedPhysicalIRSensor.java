package mazestormer.physical;

import mazestormer.robot.IRSensor;
import mazestormer.simulator.SemiPhysicalIRSensor;
import mazestormer.world.World;

public class ExtendedPhysicalIRSensor extends PhysicalIRSensor {
	
	private IRSensor virtualModule;
	
	public ExtendedPhysicalIRSensor(PhysicalCommunicator communicator, World world) {
		super(communicator);
		this.virtualModule = new SemiPhysicalIRSensor(world);
	}
	
	@Override
	public boolean hasReading() {
		return super.hasReading() || getVirtualModule().hasReading();
	}

	@Override
	public float getAngle() {
		float p = super.getAngle();
		float v = getVirtualModule().getAngle();
		
		if (Float.isNaN(p)) {
			return v;
		} 
		if (Float.isNaN(v)) {
			return p;
		}
		if (Math.abs(p) > Math.abs(v)) {
			return v;
		}
		return p;
	}
	
	private IRSensor getVirtualModule() {
		return this.virtualModule;
	}

}
