package mazestormer.robot;

import lejos.nxt.LightSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.RemoteMotor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RotatingRangeScanner;

public class PhysicalRobot implements Robot {

	private Pilot pilot;
	private LightSensor light;
	private RangeScanner scanner;

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

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			RangeFinder sensor = new UltrasonicSensor(RemoteSensorPort.get(1));
			RemoteMotor headMotor = CachedRemoteMotor.get(2);
			scanner = new RotatingRangeScanner(headMotor, sensor);
		}
		return scanner;
	}

}
