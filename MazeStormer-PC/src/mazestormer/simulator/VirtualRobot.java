package mazestormer.simulator;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.maze.Maze;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class VirtualRobot implements Robot {

	private final Maze maze;
	private VirtualPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	public VirtualRobot(Maze maze) {
		this.maze = maze;
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new VirtualPilot(Pilot.trackWidth);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null){
			this.light = new DelegatedCalibratedLightSensor(new VirtualLightSensor(this.maze, this.poseProvider));
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if(scanner == null){
			this.scanner = new VirtualRangeScanner(this.maze, this.poseProvider);
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
