package mazestormer.simulator;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class VirtualRobot implements Robot {

	private SimulatedPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new SimulatedPilot(Pilot.trackWidth);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			// TODO Implement virtual sensor
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			// TODO Implement virtual scanner
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

}
