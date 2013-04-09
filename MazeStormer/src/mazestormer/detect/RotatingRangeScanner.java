package mazestormer.detect;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RegulatedMotor;
import lejos.util.Delay;
import mazestormer.robot.ObservableRangeScanner;
import mazestormer.robot.RangeScannerListener;

public class RotatingRangeScanner extends lejos.robotics.RotatingRangeScanner
		implements ObservableRangeScanner {

	protected float gearRatio;
	private final List<RangeScannerListener> listeners = new ArrayList<RangeScannerListener>();

	public RotatingRangeScanner(RegulatedMotor head, RangeFinder rangeFinder) {
		this(head, rangeFinder, 1);
	}

	public RotatingRangeScanner(RegulatedMotor head, RangeFinder rangeFinder,
			float gearRatio) {
		super(head, rangeFinder);
		this.gearRatio = gearRatio;
	}

	/**
	 * Set the gear ratio
	 * 
	 * @param gearRatio
	 *            the gear ratio
	 */
	public void setGearRatio(float gearRatio) {
		this.gearRatio = gearRatio;
	}

	@Override
	public RangeReadings getRangeValues() {
		if (readings == null || readings.getNumReadings() != angles.length) {
			readings = new RangeReadings(angles.length);
		}

		for (int i = 0; i < angles.length; i++) {
			// Rotate and scan
			final float angle = angles[i];
			head.rotateTo((int) (angle * gearRatio));
			Delay.msDelay(50);
			float range = rangeFinder.getRange() + ZERO;
			if (range > MAX_RELIABLE_RANGE_READING) {
				range = -1;
			}
			// Make reading and trigger listeners
			final RangeReading reading = new RangeReading(angle, range);
			readings.set(i, reading);
			fireReadingReceived(reading);
		}
		// Reset head
		head.rotateTo(0);

		return readings;
	}

	@Override
	public void addListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	private void fireReadingReceived(RangeReading reading) {
		for (RangeScannerListener listener : listeners) {
			listener.readingReceived(reading);
		}
	}

}
