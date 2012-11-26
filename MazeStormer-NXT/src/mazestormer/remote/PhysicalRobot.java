package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.ShutdownCommandListener;
import mazestormer.condition.Condition;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.robot.SoundPlayer;

public class PhysicalRobot extends NXTComponent implements Robot {

	private PhysicalPilot pilot;
	private PhysicalLightSensor light;
	private PhysicalRangeScanner scanner;
	private PhysicalSoundPlayer soundPlayer;
	private PoseProvider poseProvider;

	public PhysicalRobot(NXTCommunicator communicator) {
		super(communicator);
		setup();
	}

	@Override
	public Pilot getPilot() {
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		return scanner;
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public RangeFeatureDetector getRangeDetector() {
		return null;
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
		return soundPlayer;
	}

	public void setup() {
		// Pilot
		pilot = new PhysicalPilot(getCommunicator());

		// Light sensor
		light = new PhysicalLightSensor(getCommunicator(), SensorPort.S1);

		// Scanner
		RangeFinder sensor = new UltrasonicSensor(SensorPort.S2);
		RegulatedMotor headMotor = Motor.C;
		scanner = new PhysicalRangeScanner(getCommunicator(), headMotor, sensor);

		// Sound player
		soundPlayer = new PhysicalSoundPlayer(getCommunicator());

		// Command listeners
		addMessageListener(new ShutdownCommandListener(this));
	}

	@Override
	public void terminate() {
		// Stop all communications
		getCommunicator().stop();
		// Release resources
		pilot.terminate();
		light.terminate();
		scanner.terminate();
		soundPlayer.terminate();
		// Remove registered message listeners
		super.terminate();
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public CommandBuilder when(Condition condition) {
		return null;
	}

}
