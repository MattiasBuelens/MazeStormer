package mazestormer.simulator;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.Maze;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class VirtualRobot implements Robot {

	private VirtualPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	private final Maze maze;

	public VirtualRobot(Maze maze) {
		this.maze = maze;
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new VirtualPilot(Robot.trackWidth);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new DelegatedCalibratedLightSensor(new VirtualLightSensor(
					maze, poseProvider));
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new VirtualRangeScanner(maze, poseProvider);
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
