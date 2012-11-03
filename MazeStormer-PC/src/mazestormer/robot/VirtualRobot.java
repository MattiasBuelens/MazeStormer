package mazestormer.robot;

import lejos.robotics.RangeScanner;

public class VirtualRobot implements Robot {

	private SimulatedPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new SimulatedPilot(Pilot.trackWidth);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			// TODO Implement virtual sensor
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			// TODO Implement virtual scanner
		}
		return scanner;
	}

}
