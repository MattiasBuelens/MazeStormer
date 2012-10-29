package mazestormer.robot;

import lejos.nxt.LightSensor;

public class PhysicalRobot implements Robot {

	private Pilot pilot;
	private LightSensor light;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new PhysicalPilot(Pilot.leftWheelDiameter,
					Pilot.rightWheelDiameter, Pilot.trackWidth,
					CachedRemoteMotor.get(0), CachedRemoteMotor.get(1), false);
		}
		return pilot;
	}

	@Override
	public LightSensor getLightSensor() {
		if (light == null) {
			light = new LightSensor(RemoteSensorPort.get(0));
		}
		return light;
	}

}
