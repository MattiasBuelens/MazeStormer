package mazestormer.simulator;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.maze.Maze;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;
import mazestormer.robot.SoundPlayer;

public class VirtualRobot implements Robot {

	private VirtualPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private RangeScannerFeatureDetector detector;
	private PoseProvider poseProvider;
	private VirtualCollisionDetector collisionDetect;

	private final Maze maze;

	public VirtualRobot(Maze maze) {
		this.maze = maze;
		this.collisionDetect = new VirtualCollisionDetector(maze, getPoseProvider());
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
					maze, getPoseProvider()));
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new VirtualRangeScanner(maze, getPoseProvider());
		}
		return scanner;
	}

	@Override
	public RangeScannerFeatureDetector getRangeDetector() {
		if (detector == null) {
			detector = new RangeScannerFeatureDetector(getRangeScanner());
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
	public SoundPlayer getSoundPlayer(){
		return VirtualSoundPlayer.getInstance();
	}

	@Override
	public void terminate() {
		pilot.terminate();
	}

}
