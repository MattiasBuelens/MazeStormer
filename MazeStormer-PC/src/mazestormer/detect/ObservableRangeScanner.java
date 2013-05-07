package mazestormer.detect;

import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import mazestormer.robot.RangeScannerListener;
import mazestormer.util.Future;

public interface ObservableRangeScanner extends RangeScanner {

	/**
	 * Take a set of range readings asynchronously.
	 */
	public Future<RangeReadings> getRangeValuesAsync();

	/**
	 * Add a range scanner listener.
	 * 
	 * @param listener
	 */
	public void addListener(RangeScannerListener listener);

	/**
	 * Remove a range scanner listener.
	 * 
	 * @param listener
	 */
	public void removeListener(RangeScannerListener listener);

}
