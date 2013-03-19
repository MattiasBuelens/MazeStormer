package mazestormer.detect;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RegulatedMotor;
import lejos.util.Delay;

public class RotatingRangeScanner extends lejos.robotics.RotatingRangeScanner {

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
			head.rotateTo((int) (angles[i] * gearRatio));
			Delay.msDelay(50);
			float range = rangeFinder.getRange() + ZERO;
			if (range > MAX_RELIABLE_RANGE_READING) {
				range = -1;
			}
			readings.setRange(i, angles[i], range);
		}
		head.rotateTo(0);
		return readings;
	}

	protected float gearRatio;

}
