package mazestormer.robot;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;

public interface Robot {

	public Pilot getPilot();

	public CalibratedLightSensor getLightSensor();

	public RangeScanner getRangeScanner();

	public PoseProvider getPoseProvider();

}
