package mazestormer.detect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Comparator;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.robot.Robot;
import mazestormer.simulator.VirtualRangeScanner;

/**
 * A range feature detector which uses a range scanner to locate objects.
 */
public class RangeScannerFeatureDetector extends AbstractFeatureDetector
		implements RangeFeatureDetector {

	private static final float defaultMaxDistance = 100f;

	private final RangeScanner scanner;
	private final Point offset;
	private float maxDistance;
	private PoseProvider pp = null;

	public RangeScannerFeatureDetector(RangeScanner scanner, float maxDistance,
			Point offset) {
		this.scanner = checkNotNull(scanner);
		this.maxDistance = maxDistance;
		this.offset = offset;
	}

	public RangeScannerFeatureDetector(RangeScanner scanner, float maxDistance) {
		this(scanner, maxDistance, Robot.sensorPosition);
	}

	public RangeScannerFeatureDetector(RangeScanner scanner) {
		this(scanner, defaultMaxDistance);
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

			RangeReading reading;
			// no need to change the coordinate system here.
			// the point (0,0) is already the rotation center of the robot.
			if (scanner instanceof VirtualRangeScanner)
				reading = rawReading;
			else {
				// Change coordinate system from sensor (where O is the rotation
				// center of the sensor-servo)
				// to nxt (where 0 is the rotation center of the robot)
				Point position = Robot.sensorPosition.pointAt(
						rawReading.getRange(), rawReading.getAngle());
				float angle = (float) Math.toDegrees(position.angle());
				float range = position.length();
				reading = new RangeReading(angle, range);
			}

			// Only retain readings smaller than the maximum distance
			if (reading.getRange() <= getMaxDistance()) {
				// Sort the filtered readings
				int index = Collections.binarySearch(readings, reading,
						comparator);
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
