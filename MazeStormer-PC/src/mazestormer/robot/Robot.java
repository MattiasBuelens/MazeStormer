package mazestormer.robot;

import lejos.nxt.LightSensor;
import lejos.robotics.RangeScanner;

public interface Robot {

	public Pilot getPilot();

	public LightSensor getLightSensor();

	public RangeScanner getRangeScanner();

}
