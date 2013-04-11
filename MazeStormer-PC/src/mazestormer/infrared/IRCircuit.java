package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class IRCircuit implements VirtualIRSource {

	private final PoseProvider staticPoseProvider;
	private final Envelope envelope;
	
	//TODO: cm's?
	public static final double HEIGHT = 0;
	public static final double WIDTH = 0;
	
	public IRCircuit(Pose pose) {
		this.staticPoseProvider = new StaticPoseProvider(pose);
		this.envelope = new RectangularEnvelope(this.staticPoseProvider, HEIGHT, WIDTH);
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public Envelope getEnvelope() {
		return this.envelope;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return this.staticPoseProvider;
	}
}
