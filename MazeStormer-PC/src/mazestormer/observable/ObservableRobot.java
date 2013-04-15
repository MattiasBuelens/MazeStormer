package mazestormer.observable;

import lejos.robotics.localization.PoseProvider;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRRobot;
import mazestormer.infrared.RectangularEnvelope;
import mazestormer.robot.Robot;
import mazestormer.world.ModelType;

public class ObservableRobot implements Robot, IRRobot {

	private PoseProvider poseProvider;
	
	private final Envelope envelope;
	private final ModelType modelType;

	public ObservableRobot(ModelType modelType) {
		poseProvider = new ObservePoseProvider();
		this.modelType = modelType;
		
		//TODO
		this.envelope = new RectangularEnvelope(0+EXTERNAL_ZONE, 0+EXTERNAL_ZONE);
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
}
