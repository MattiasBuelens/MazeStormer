package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class PhysicalRobot implements Robot {

	private PhysicalPilot pilot;
	private PhysicalLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new PhysicalPilot(Robot.leftWheelDiameter,
					Robot.rightWheelDiameter, Robot.trackWidth,
					Motor.A, Motor.B, false);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new PhysicalLightSensor(SensorPort.S1);
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			RangeFinder sensor = new UltrasonicSensor(SensorPort.S2);
			RegulatedMotor headMotor = Motor.C;
			scanner = new RotatingRangeScanner(headMotor, sensor);
		}
		return scanner;
	}

	@Override
	public PoseProvider getPoseProvider() {
		if (poseProvider == null) {
			poseProvider = new OdometryPoseProvider(getPilot());
		}
		return poseProvider;
	}

	@Override
	public void terminate() {
		pilot.terminate();
	}

}
