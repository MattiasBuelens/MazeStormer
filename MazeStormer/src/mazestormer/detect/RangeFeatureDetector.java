package mazestormer.detect;

import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.util.Future;

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

	/**
	 * Performs a single scan for an object and returns the results
	 * asynchronously.
	 */
	public Future<RangeFeature> scanAsync();

	/**
	 * Performs a single scan for an object at the given angles and returns the
	 * results. If an object is not detected, this method returns <b>null</b>.
	 * 
	 * @param angles
	 *            The angles to scan at.
	 * 
	 * @see {@link #scan()}
	 */
	public RangeFeature scan(float[] angles);

	/**
	 * Performs a single scan for an object at the given angles and returns the
	 * results asynchronously.
	 * 
	 * @param angles
	 *            The angles to scan at.
	 * @see {@link #scanAsync()}
	 */
	public Future<RangeFeature> scanAsync(float[] angles);

	/**
	 * Add a range feature listener.
	 * 
	 * @param listener
	 */
	public void addListener(RangeFeatureListener listener);

	/**
	 * Remove a range feature listener.
	 * 
	 * @param listener
	 */
	public void removeListener(RangeFeatureListener listener);

}
