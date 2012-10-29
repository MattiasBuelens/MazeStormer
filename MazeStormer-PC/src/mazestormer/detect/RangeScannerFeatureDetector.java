package mazestormer.detect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Comparator;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetectorAdapter;
import lejos.robotics.objectdetection.RangeFeature;

/**
 * A range feature detector which uses a range scanner to locate objects.
 */
public class RangeScannerFeatureDetector extends FeatureDetectorAdapter {

	private static final float defaultMaxDistance = 100f;

	private final RangeScanner scanner;
	private float maxDistance;
	private PoseProvider pp = null;

	public RangeScannerFeatureDetector(RangeScanner scanner, float maxDistance, int delay) {
		super(delay);
		this.scanner = checkNotNull(scanner);
		this.maxDistance = maxDistance;
	}

	public RangeScannerFeatureDetector(RangeScanner scanner, int delay) {
		this(scanner, defaultMaxDistance, delay);
	}

	/**
	 * Returns the maximum distance this detector will return for detected features.
	 * 
	 * @return The maximum distance.
	 */
	public float getMaxDistance() {
		return this.maxDistance;
	}

	/**
	 * Sets the maximum distance to register detected objects from the range scanner.
	 * 
	 * @param distance
	 * 			The maximum distance.
	 */
	public void setMaxDistance(float distance) {
		this.maxDistance = distance;
	}

	/**
	 * Set the pose provider to register the current pose when registering new readings.
	 * 
	 * @param pp
	 * 			The new pose provider.
	 */
	public void setPoseProvider(PoseProvider pp) {
		this.pp = pp;
	}

	@Override
	public Feature scan() {
		// Get the range readings
		RangeReadings rawReadings = scanner.getRangeValues();

		// Filter and sort the readings
		Comparator<RangeReading> comparator = new RangeReadingComparator();
		RangeReadings readings = new RangeReadings(0);

		for (RangeReading reading : rawReadings) {
			// Only retain positive readings smaller than the maximum distance
			if (reading.getRange() > 0 && reading.getRange() <= getMaxDistance()) {
				// Sort the filtered readings
				int index = Collections.binarySearch(readings, reading, comparator);
				if (index < 0) {
					index = -index - 1;
				}
				readings.add(index, reading);
			}
		}

		// Check to make sure it retrieved some readings
		if (readings.isEmpty())
			return null;

		// Add current pose if pose provider available
		if (pp != null) {
			return new RangeFeature(readings, pp.getPose());
		} else {
			return new RangeFeature(readings);
		}
	}

}
