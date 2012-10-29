package mazestormer.detect;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RotatingRangeScanner;
import mazestormer.robot.CachedRemoteMotor;

public class PhysicalRangeScanner extends RotatingRangeScanner {

	public PhysicalRangeScanner(int gearRatio) {
		super(CachedRemoteMotor.get(3), new UltrasonicSensor(SensorPort.S2),
				gearRatio);
	}

	public PhysicalRangeScanner() {
		this(1);
	}

}
