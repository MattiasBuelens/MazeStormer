package mazestormer.robot;

import lejos.robotics.RangeScanner;

public interface Robot {

	public Pilot getPilot();

	public CalibratedLightSensor getLightSensor();

	public RangeScanner getRangeScanner();

}
