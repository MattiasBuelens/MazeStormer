package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRRobot;
import mazestormer.infrared.RectangularEnvelope;
import mazestormer.world.ModelType;

public class ObservableRobot implements IRRobot {

	private PoseProvider poseProvider;

	private final Envelope envelope;
	private final ModelType modelType;
	private final double width;
	private final double height;

	public ObservableRobot(ModelType modelType, double width, double height) {
		poseProvider = new ObservePoseProvider();
		this.modelType = modelType;
		this.width = width;
		this.height = height;

		// TODO
		this.envelope = new RectangularEnvelope(0 + EXTERNAL_ZONE,
				0 + EXTERNAL_ZONE);
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
