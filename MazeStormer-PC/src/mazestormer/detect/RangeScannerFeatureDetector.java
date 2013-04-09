package mazestormer.detect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.objectdetection.RangeFeature;

/**
 * A range feature detector which uses a range scanner to locate objects.
 */
public class RangeScannerFeatureDetector extends AbstractFeatureDetector implements RangeFeatureDetector {

	private final RangeScanner scanner;
	private final Point offset;
	private float maxDistance;
	private PoseProvider pp = null;
	private final List<RangeFeatureListener> listeners = new ArrayList<RangeFeatureListener>();

	public RangeScannerFeatureDetector(RangeScanner scanner, float maxDistance, Point offset) {
		this.scanner = checkNotNull(scanner);
		this.maxDistance = maxDistance;
		this.offset = offset;
	}

	/**
	 * Get the range scanner used by this feature detector.
	 */
	public RangeScanner getScanner() {
		return scanner;
	}

	@Override
	public float getMaxDistance() {
		return this.maxDistance;
	}

	@Override
	public void setMaxDistance(float distance) {
		this.maxDistance = distance;
	}

	/**
	 * Get the relative offset of the range finder from the center of the robot,
	 * in centimeters.
	 */
	public Point getOffset() {
		return offset;
	}

	/**
	 * Set the pose provider to register the current pose when registering new
	 * readings.
	 * 
	 * @param pp
	 *            The new pose provider.
	 */
	public void setPoseProvider(PoseProvider pp) {
		this.pp = pp;
	}

	@Override
	public RangeFeature scan() {
		// Get the range readings
		RangeReadings rawReadings = scanner.getRangeValues();
		if (rawReadings == null)
			return null;

		// Filter and sort the readings
		Comparator<RangeReading> comparator = new ReadingRangeComparator();
		RangeReadings readings = new RangeReadings(0);

		for (RangeReading rawReading : rawReadings) {
			// Only retain positive readings
			if (rawReading.getRange() < 0)
				continue;

			// Change coordinate system from sensor with the origin in the
			// rotation center of the sensor-servo to NXT with the origin in the
			// rotation center of the robot
			Point position = getOffset().pointAt(rawReading.getRange(), rawReading.getAngle());
			float angle = (float) Math.toDegrees(position.angle());
			float range = position.length();
			RangeReading reading = new RangeReading(angle, range);

			// Only retain readings smaller than the maximum distance
			if (reading.getRange() <= getMaxDistance()) {
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
		RangeFeature feature;
		if (pp != null) {
			feature = new RangeFeature(readings, pp.getPose());
		} else {
			feature = new RangeFeature(readings);
		}

		// Trigger listeners
		fireFeatureReceived(feature);

		return feature;
	}

	/**
	 * @effect The scan angles of the range scanner are set to the given angles.
	 *         | getScanner().setAngles(angles)
	 */
	@Override
	public RangeFeature scan(float[] angles) {
		getScanner().setAngles(angles);
		return scan();
	}

	@Override
	public void addListener(RangeFeatureListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(RangeFeatureListener listener) {
		listeners.remove(listener);
	}

	private void fireFeatureReceived(RangeFeature feature) {
		for (RangeFeatureListener listener : listeners) {
			listener.featureReceived(feature);
		}
	}

}
