package mazestormer.world;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import mazestormer.infrared.OffsettedPoseProvider;
import mazestormer.infrared.OffsettedPoseProvider.Module;
import mazestormer.infrared.StaticPoseProvider;

public class Item implements Model {
	
	private PoseProvider poseProvider;
	
	public Item(Pose pose) {
		this.poseProvider = new StaticPoseProvider(pose);
	}
	
	public void capture(PoseProvider hunter) {
		this.poseProvider = new OffsettedPoseProvider(hunter, Module.ITEM);
	}

	@Override
	public PoseProvider getPoseProvider() {
		return this.poseProvider;
	}

	@Override
	public ModelType getModelType() {
		return ModelType.VIRTUAL;
	}

}
