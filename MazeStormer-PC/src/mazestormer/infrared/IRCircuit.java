package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.world.ModelType;

public class IRCircuit implements IRSource {

	private final PoseProvider staticPoseProvider;
	private final Envelope envelope;
	
	public static final double RADIUS = 80.0;
	
	public IRCircuit(Pose pose) {
		this.staticPoseProvider = new StaticPoseProvider(pose);
		this.envelope = new CircularEnvelope(RADIUS);
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

	@Override
	public ModelType getModelType() {
		return ModelType.VIRTUAL;
	}
}
