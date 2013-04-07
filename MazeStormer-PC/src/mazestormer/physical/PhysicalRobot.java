package mazestormer.physical;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.CommandType;
import mazestormer.condition.Condition;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.SoundPlayer;
import mazestormer.world.World;

public class PhysicalRobot extends PhysicalComponent implements
		ControllableRobot {

	/**
	 * Timeout for synchronous requests.
	 */
	public static final int requestTimeout = 10000;
	
	private World world;

	private PhysicalPilot pilot;
	private PoseProvider poseProvider;

	private PhysicalLightSensor light;

	private RangeScanner scanner;
	private RangeScannerFeatureDetector detector;
	private IRSensor irSensor;

	private SoundPlayer soundPlayer;

	public PhysicalRobot(PhysicalCommunicator communicator) {
		super(communicator);
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new PhysicalPilot(getCommunicator());
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new PhysicalLightSensor(getCommunicator());
		}
		return light;
	}

	// @Override
	protected RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new PhysicalRangeScanner(getCommunicator());
		}
		return scanner;
	}
	
	private World getWorld() {
		return this.world;
	}
	
	@Override
	public IRSensor getIRSensor() {
		if (irSensor == null) {
			irSensor = new ExtendedPhysicalIRSensor(getCommunicator(), getWorld());
		}
		return irSensor;
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
			soundPlayer = new PhysicalSoundPlayer(getCommunicator());
		}
		return soundPlayer;
	}

	@Override
	public CommandBuilder when(Condition condition) {
		PhysicalCommandBuilder builder = new PhysicalCommandBuilder(
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
