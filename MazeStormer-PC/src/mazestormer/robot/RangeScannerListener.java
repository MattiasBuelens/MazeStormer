package mazestormer.robot;

import lejos.robotics.RangeReading;

public interface RangeScannerListener {

	public void readingReceived(RangeReading reading);

}
