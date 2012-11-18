package mazestormer.remote;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.simulator.DelegatedCalibratedLightSensor;

public class RemoteRobot extends RemoteComponent implements Robot {

	private RemotePilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	public RemoteRobot(RemoteCommunicator communicator) {
		super(communicator);
		setup();
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
			light = new DelegatedCalibratedLightSensor(new RemoteLightSensor(getCommunicator()));
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new RemoteRangeScanner();
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

	public void setup() {
		// Communicator<Command, Report> comm = getCommunicator();

		// Report listeners

		// Commanders
	}

	@Override
	public void terminate() {
		// Terminate the pilot
		getPilot().terminate();
		// Stop all communications
		getCommunicator().stop();
		// Remove registered message listeners
		super.terminate();
	}

}
