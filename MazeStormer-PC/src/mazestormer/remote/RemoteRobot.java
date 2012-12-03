package mazestormer.remote;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.CommandType;
import mazestormer.condition.Condition;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.robot.SoundPlayer;

public class RemoteRobot extends RemoteComponent implements Robot {

	/**
	 * Timeout for synchronous requests.
	 */
	public static final int requestTimeout = 10000;

	private RemotePilot pilot;
	private PoseProvider poseProvider;

	private RemoteLightSensor light;

	private RangeScanner scanner;
	private RangeScannerFeatureDetector detector;

	private SoundPlayer soundPlayer;

	public RemoteRobot(RemoteCommunicator communicator) {
		super(communicator);
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new RemotePilot(getCommunicator());
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new RemoteLightSensor(getCommunicator());
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new RemoteRangeScanner(getCommunicator());
		}
		return scanner;
	}

	@Override
	public RangeFeatureDetector getRangeDetector() {
		if (detector == null) {
			detector = new RangeScannerFeatureDetector(getRangeScanner(),
					sensorMaxDistance, sensorPosition);
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
			soundPlayer = new RemoteSoundPlayer(getCommunicator());
		}
		return soundPlayer;
	}

	@Override
	public CommandBuilder when(Condition condition) {
		RemoteCommandBuilder builder = new RemoteCommandBuilder(
				getCommunicator(), CommandType.WHEN, condition);
		addMessageListener(builder);
		return builder;
	}

	@Override
	public void terminate() {
		// Terminate components
		getPilot().terminate();
		if (light != null)
			light.terminate();
		// Stop all communications
		getCommunicator().stop();
		// Remove registered message listeners
		super.terminate();
	}

}
