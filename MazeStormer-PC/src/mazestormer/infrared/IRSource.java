package mazestormer.infrared;

import lejos.robotics.localization.PoseProvider;

public interface IRSource {
	
	public PoseProvider getPoseProvider();
	
	public boolean isEmitting();
	
	public ExtendedRectangle2D getEnvelope();
}
