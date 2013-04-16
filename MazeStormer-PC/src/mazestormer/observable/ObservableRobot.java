package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.infrared.CircularEnvelope;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRRobot;
import mazestormer.world.ModelType;

public class ObservableRobot implements IRRobot {

	private PoseProvider poseProvider;

	private final Envelope envelope;
	private final ModelType modelType;
	private final double width;
	private final double height;

	public ObservableRobot(ModelType modelType, double height, double width) {
		poseProvider = new ObservePoseProvider();
		this.modelType = modelType;
		this.width = width;
		this.height = height;

		this.envelope = new CircularEnvelope(INTERNAL_RADIUS, EXTERNAL_RADIUS);
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
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
	public ModelType getModelType() {
		return this.modelType;
	}

	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

}
