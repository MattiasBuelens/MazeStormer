package mazestormer.world;

import lejos.robotics.localization.PoseProvider;

public interface Model {

	public PoseProvider getPoseProvider();

	public ModelType getModelType();

}
