package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;

public class IRBall implements IRSource {
	
	private final PoseProvider staticPoseProvider;
	private final Envelope envelope;
	
	//TODO: cm's?
	public static final double INTERNAL_RADIUS = (7.5)/2;
	public static final double EXTERNAL_RADIUS = 5000;
	
	public IRBall(Pose pose) {
		this.staticPoseProvider = new StaticPoseProvider(pose);
		this.envelope = new CircularEnvelope(this.staticPoseProvider, INTERNAL_RADIUS+EXTERNAL_RADIUS);
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
