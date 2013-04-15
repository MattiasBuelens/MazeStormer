package mazestormer.simulator;

import lejos.geom.Point;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.detect.ObservableRangeScanner;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.infrared.Envelope;
import mazestormer.infrared.IRRobot;
import mazestormer.infrared.RectangularEnvelope;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllablePCRobot;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.RobotUpdateListener;
import mazestormer.robot.SoundPlayer;
import mazestormer.simulator.collision.CollisionObserver;
import mazestormer.simulator.collision.VirtualCollisionDetector;
import mazestormer.world.ModelType;
import mazestormer.world.World;

public class VirtualRobot implements ControllablePCRobot, IRRobot {

	private final World world;

	private final VirtualPilot pilot;
	private final PoseProvider poseProvider;
	private final double width = robotWidth;
	private final double height = robotHeight;

	private final CalibratedLightSensor light;
	private final ObservableRangeScanner rangeScanner;
	private final RangeScannerFeatureDetector rangeDetector;
	private final VirtualIRSensor infrared;

	private final SoundPlayer soundPlayer;

	private final VirtualCollisionDetector collisionDetector;
	private final CollisionObserver collisionObserver;

	private final VirtualConditionResolvers conditionResolvers;

	private final VirtualUpdateProducer updateProducer;
	
	private final Envelope envelope;

	public VirtualRobot(World world) {
		this.world = world;

		// Pilot
		pilot = new VirtualPilot(ControllableRobot.trackWidth);
		poseProvider = new OdometryPoseProvider(pilot);

		// Light sensor
		light = new VirtualLightSensor(world);

		// Range scanner
		rangeScanner = new VirtualRangeScanner(getWorld());
		rangeDetector = new RangeScannerFeatureDetector(rangeScanner, sensorMaxDistance, new Point(0f, 0f));
		rangeDetector.setPoseProvider(getPoseProvider());

		// Infrared sensor
		infrared = new VirtualIRSensor(world);

		// Sound player
		soundPlayer = new VirtualSoundPlayer();

		// Collision detector
		collisionDetector = new VirtualCollisionDetector(world);
		collisionObserver = new CollisionObserver(this);

		// Conditional commands
		conditionResolvers = new VirtualConditionResolvers(this);

		// Updates
		updateProducer = new VirtualUpdateProducer(this);
		
		//TODO
		this.envelope = new RectangularEnvelope(0+EXTERNAL_ZONE, 0+EXTERNAL_ZONE);
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	private World getWorld() {
		return world;
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
	public RangeFeatureDetector getRangeDetector() {
		return rangeDetector;
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

	public VirtualCollisionDetector getCollisionDetector() {
		return collisionDetector;
	}

	public CollisionObserver getCollisionObserver() {
		return collisionObserver;
	}

	@Override
	public void addUpdateListener(RobotUpdateListener listener) {
		updateProducer.addUpdateListener(listener);
	}

	@Override
	public void removeUpdateListener(RobotUpdateListener listener) {
		updateProducer.removeUpdateListener(listener);
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

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public Envelope getEnvelope() {
		return this.envelope;
	}

	@Override
	public ModelType getModelType() {
		return ModelType.VIRTUAL;
	}
}
