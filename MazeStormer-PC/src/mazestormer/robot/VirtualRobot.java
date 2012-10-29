package mazestormer.robot;

import lejos.nxt.LightSensor;

public class VirtualRobot implements Robot {

	private SimulatedPilot pilot;
	private LightSensor light;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new SimulatedPilot(Pilot.trackWidth);
		}
		return pilot;
	}

	@Override
	public LightSensor getLightSensor() {
		if (light == null) {
			// TODO Implement virtual sensor
		}
		return light;
	}

}
