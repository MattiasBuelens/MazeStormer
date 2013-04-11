package mazestormer.detect;

import lejos.robotics.RangeReadings;
import mazestormer.util.Future;

public interface AsyncRangeScanner extends lejos.robotics.RangeScanner {

	/**
	 * Take a set of range readings asynchronously.
	 */
	public Future<RangeReadings> getRangeValuesAsync();

}
