package mazestormer.detect;

import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.RangeFeature;

public interface RangeFeatureDetector extends FeatureDetector {

	/**
	 * Returns the maximum distance this detector will return for detected
	 * features.
	 * 
	 * @return The maximum distance.
	 */
	public float getMaxDistance();

	/**
	 * Sets the maximum distance to register detected objects from the range
	 * scanner.
	 * 
	 * @param distance
	 *            The maximum distance.
	 */
	public void setMaxDistance(float distance);

	@Override
	public RangeFeature scan();

}
