package mazestormer.simulator;

import lejos.geom.Point;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.maze.Maze;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.SoundPlayer;
import mazestormer.simulator.collision.CollisionObserver;
import mazestormer.simulator.collision.VirtualCollisionDetector;
import mazestormer.world.World;

public class VirtualRobot implements ControllableRobot {

	private VirtualPilot pilot;
	private CalibratedLightSensor light;
	private RangeScanner scanner;
	private RangeScannerFeatureDetector detector;
	private VirtualIRSensor ir;
	private SoundPlayer soundPlayer;
	private PoseProvider poseProvider;
	private final VirtualCollisionDetector collisionDetector;
	private final CollisionObserver collisionObserver;
	private final VirtualConditionResolvers conditionResolvers;

	private final World world;

	public VirtualRobot(World world) {
		this.world = world;

		this.collisionDetector = new VirtualCollisionDetector(getMaze(),
				getPoseProvider());
		this.collisionObserver = new CollisionObserver(this);

		this.conditionResolvers = new VirtualConditionResolvers(this);
	}
	
	private World getWorld() {
		return world;
	}


	private Maze getMaze() {
		return world.getMaze();
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new VirtualPilot(ControllableRobot.trackWidth);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new VirtualLightSensor(getWorld());
		}
		return light;
	}

	// @Override
	protected RangeScanner getRangeScanner() {
		if (scanner == null) {
			scanner = new VirtualRangeScanner(getWorld());
		}
		return scanner;
	}

	@Override
	public RangeFeatureDetector getRangeDetector() {
		if (detector == null) {
			detector = new RangeScannerFeatureDetector(getRangeScanner(),
					sensorMaxDistance, new Point(0f, 0f));
			detector.setPoseProvider(getPoseProvider());
		}
		return detector;
	}

	@Override
	public IRSensor getIRSensor() {
		if (ir == null) {
			ir = new VirtualIRSensor(world);
		}
		return ir;
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
			soundPlayer = new VirtualSoundPlayer();
		}
		return soundPlayer;
	}

	public VirtualCollisionDetector getCollisionDetector() {
		return collisionDetector;
	}

	public CollisionObserver getCollisionObserver() {
		return collisionObserver;
	}

	@Override
	public CommandBuilder when(Condition condition) {
		ConditionFuture future = conditionResolvers.resolve(condition);
		return new VirtualCommandBuilder(this, future);
	}

	@Override
	public void terminate() {
		pilot.terminate();
		collisionObserver.terminate();
		conditionResolvers.terminate();
	}

}
