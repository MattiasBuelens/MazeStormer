package mazestormer.robot;

import lejos.robotics.RangeScanner;

public interface ObservableRangeScanner extends RangeScanner {

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
