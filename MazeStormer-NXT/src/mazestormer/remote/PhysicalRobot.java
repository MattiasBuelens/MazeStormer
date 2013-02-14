package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.Command;
import mazestormer.command.ShutdownCommand;
import mazestormer.condition.Condition;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RotatingRangeScanner;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.SoundPlayer;

public class PhysicalRobot extends NXTComponent implements ControllableRobot,
		MessageListener<Command> {

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

	// @Override
	protected RangeScanner getRangeScanner() {
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

	private void setup() {
		final NXTCommunicator comm = getCommunicator();

		// Pilot
		pilot = new PhysicalPilot(comm);

		// Light sensor
		light = new PhysicalLightSensor(comm, SensorPort.S1);

		// Scanner
		RangeFinder ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		RegulatedMotor headMotor = Motor.C;
		float gearRatio = ControllableRobot.sensorGearRatio;
		RangeScanner headScanner = new RotatingRangeScanner(headMotor,
				ultrasonicSensor, gearRatio);
		scanner = new PhysicalRangeScanner(comm, headScanner);

		// Sound player
		soundPlayer = new PhysicalSoundPlayer(comm);

		// Command listeners
		addMessageListener(this);
	}

	@Override
	public void terminate() {
		// Stop all communications
		getCommunicator().stop();
		// Release resources
		pilot.terminate();
		light.terminate();
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

	@Override
	public void messageReceived(Command command) {
		if (command instanceof ShutdownCommand) {
			// Shut down
			terminate();
		}
	}

}
