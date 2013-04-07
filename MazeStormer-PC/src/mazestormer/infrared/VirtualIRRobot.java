package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.Robot;

public class VirtualIRRobot implements VirtualIRSource, IRRobot {
		
	private final Envelope envelope;
	
	public VirtualIRRobot(Robot robot) {
		// TODO: dimension support
		this.envelope = new RectangularEnvelope(robot.getPoseProvider(), 0+EXTERNAL_ZONE, 0+EXTERNAL_ZONE);
	}

	@Override
	public PoseProvider getPoseProvider() {
		return getEnvelope().getPoseProvider();
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public Envelope getEnvelope() {
		return this.envelope;
	}
}
