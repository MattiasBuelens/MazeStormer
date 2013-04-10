package mazestormer.detect;

import mazestormer.robot.RangeScannerListener;

public interface ObservableRangeScanner extends AsyncRangeScanner {

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
