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
import mazestormer.report.UpdateReporter;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.RobotUpdateListener;
import mazestormer.robot.SoundPlayer;

public class PhysicalRobot extends NXTComponent implements ControllableRobot,
		MessageListener<Command> {

	private final PhysicalPilot pilot;
	private final PoseProvider poseProvider;

	private final PhysicalLightSensor light;
	private final PhysicalRangeScanner scanner;
	private final PhysicalIRSensor infrared;

	private final PhysicalSoundPlayer soundPlayer;

	private final UpdateReporter updateReporter;

	public PhysicalRobot(NXTCommunicator communicator) {
		super(communicator);

		// Pilot
		pilot = new PhysicalPilot(communicator);
		poseProvider = new OdometryPoseProvider(getPilot());

		// Light sensor
		light = new PhysicalLightSensor(communicator, SensorPort.S1);

		// Scanner
		RangeFinder ultrasonicSensor = new UltrasonicSensor(SensorPort.S4);
		RegulatedMotor headMotor = Motor.C;
		float gearRatio = ControllableRobot.sensorGearRatio;
		RangeScanner headScanner = new RotatingRangeScanner(headMotor,
				ultrasonicSensor, gearRatio);
		scanner = new PhysicalRangeScanner(communicator, headScanner);

		// Infrared
		infrared = new PhysicalIRSensor(communicator, SensorPort.S3);

		// Sound player
		soundPlayer = new PhysicalSoundPlayer(communicator);

		// Command listener
		addMessageListener(this);

		// Start reporting updates
		updateReporter = new UpdateReporter(communicator, this);
		updateReporter.start();
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
	public IRSensor getIRSensor() {
		return infrared;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	@Override
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public void addUpdateListener(RobotUpdateListener listener) {
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public void removeUpdateListener(RobotUpdateListener listener) {
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public CommandBuilder when(Condition condition) {
		return null;
	}

	@Override
	public void terminate() {
		// Stop reporting updates
		updateReporter.stop();
		// Stop all communications
		getCommunicator().stop();
		// Release resources
		pilot.terminate();
		light.terminate();
		// Remove registered message listeners
		super.terminate();
	}

	@Override
	public void messageReceived(Command command) {
		if (command instanceof ShutdownCommand) {
			// Shut down
			terminate();
		}
	}

}
