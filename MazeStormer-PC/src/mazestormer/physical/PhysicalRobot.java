package mazestormer.physical;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.CommandType;
import mazestormer.condition.Condition;
import mazestormer.detect.ObservableRangeScanner;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.remote.MessageListener;
import mazestormer.report.Report;
import mazestormer.report.UpdateReport;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.RobotUpdate;
import mazestormer.robot.RobotUpdateListener;
import mazestormer.robot.SoundPlayer;
import mazestormer.world.World;

public class PhysicalRobot extends PhysicalComponent implements ControllableRobot {

	/**
	 * Timeout for synchronous requests.
	 */
	public static final int requestTimeout = 10000;

	private final PhysicalPilot pilot;
	private final PoseProvider poseProvider;

	private final PhysicalLightSensor light;
	private final ObservableRangeScanner rangeScanner;
	private final RangeScannerFeatureDetector rangeDetector;
	private final IRSensor infrared;

	private final SoundPlayer soundPlayer;

	private final UpdateReceiver updateReceiver;
	private final List<RobotUpdateListener> updateListeners = new ArrayList<RobotUpdateListener>();

	public PhysicalRobot(PhysicalCommunicator communicator, World world) {
		super(communicator);

		// Updates
		updateReceiver = new UpdateReceiver();
		communicator.addListener(updateReceiver);

		// Pilot
		pilot = new PhysicalPilot(communicator);
		addUpdateListener(pilot);
		poseProvider = new OdometryPoseProvider(pilot);

		// Light sensor
		light = new PhysicalLightSensor(communicator);

		// Range scanner
		rangeScanner = new PhysicalRangeScanner(communicator);
		rangeDetector = new RangeScannerFeatureDetector(rangeScanner, sensorMaxDistance, sensorPosition);
		rangeDetector.setPoseProvider(poseProvider);

		// Infrared sensor
		infrared = new ExtendedPhysicalIRSensor(getCommunicator(), world);

		// Sound player
		soundPlayer = new PhysicalSoundPlayer(communicator);
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
	public ObservableRangeScanner getRangeScanner() {
		return rangeScanner;
	}

	@Override
	public IRSensor getIRSensor() {
		return infrared;
	}

	@Override
	public RangeFeatureDetector getRangeDetector() {
		return rangeDetector;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	@Override
	public SoundPlayer getSoundPlayer() {
		return soundPlayer;
	}

	@Override
	public void addUpdateListener(RobotUpdateListener listener) {
		updateListeners.add(listener);
	}

	@Override
	public void removeUpdateListener(RobotUpdateListener listener) {
		updateListeners.remove(listener);
	}

	private void updateReceived(RobotUpdate update) {
		for (RobotUpdateListener listener : updateListeners) {
			listener.updateReceived(update);
		}
	}

	@Override
	public CommandBuilder when(Condition condition) {
		PhysicalCommandBuilder builder = new PhysicalCommandBuilder(getCommunicator(), CommandType.WHEN, condition);
		addMessageListener(builder);
		return builder;
	}

	@Override
	public void terminate() {
		// Terminate components
		getPilot().terminate();
		light.terminate();
		// Stop all communications
		getCommunicator().stop();
		// Remove registered message listeners
		super.terminate();
	}

	private class UpdateReceiver implements MessageListener<Report<?>> {

		@Override
		public void messageReceived(Report<?> message) {
			if (message instanceof UpdateReport) {
				updateReceived(((UpdateReport) message).getValue());
			}
		}

	}

}
