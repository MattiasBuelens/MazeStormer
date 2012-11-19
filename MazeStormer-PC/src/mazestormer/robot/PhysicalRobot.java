package mazestormer.robot;

import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.RemoteMotor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;

public class PhysicalRobot implements Robot {

	private PhysicalPilot pilot;
	private PhysicalLightSensor light;
	private RangeScanner scanner;
	private RangeScannerFeatureDetector detector;
	private SoundPlayer soundPlayer;
	private PoseProvider poseProvider;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new PhysicalPilot(Robot.leftWheelDiameter,
					Robot.rightWheelDiameter, Robot.trackWidth,
					CachedRemoteMotor.get(0), CachedRemoteMotor.get(1), false);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new PhysicalLightSensor(RemoteSensorPort.get(0));
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

	@Override
	public RangeFeatureDetector getRangeDetector() {
		if (detector == null) {
			detector = new RangeScannerFeatureDetector(getRangeScanner());
			detector.setPoseProvider(getPoseProvider());
		}
		return detector;
	}

	@Override
	public PoseProvider getPoseProvider() {
		if (poseProvider == null) {
			poseProvider = new OdometryPoseProvider(getPilot());
		}
		return poseProvider;
	}

	@Override
	public SoundPlayer getSoundPlayer() {
		if (soundPlayer == null) {
			soundPlayer = new PhysicalSoundPlayer();
		}
		return soundPlayer;
	}

	@Override
	public CommandBuilder when(ConditionSource source,
			CompareOperator operator, double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void terminate() {
		pilot.terminate();
	}

}
