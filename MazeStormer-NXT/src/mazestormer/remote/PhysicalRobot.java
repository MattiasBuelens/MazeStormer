package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.util.Delay;
import mazestormer.command.Command;
import mazestormer.command.ShutdownCommand;
import mazestormer.condition.Condition;
import mazestormer.detect.RangeFeatureDetector;
import mazestormer.report.ReportType;
import mazestormer.report.UpdateReport;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.robot.IRSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.RobotUpdate;
import mazestormer.robot.RobotUpdateListener;

public class PhysicalRobot extends NXTComponent implements ControllableRobot, MessageListener<Command>, Runnable {

	private final PhysicalPilot pilot;
	private final PoseProvider poseProvider;
	private final double width = robotWidth;
	private final double height = robotHeight;

	private final PhysicalLightSensor light;
	private final PhysicalRangeScanner scanner;
	private final PhysicalIRSensor infrared;

	private Thread thread;
	private boolean isRunning = false;

	public PhysicalRobot(NXTCommunicator communicator) {
		super(communicator);

		// Pilot
		pilot = new PhysicalPilot(communicator);
		poseProvider = new OdometryPoseProvider(getPilot());

		// Light sensor
		light = new PhysicalLightSensor(communicator, SensorPort.S1);
		light.setFloodlight(true);

		// Scanner
		RangeFinder ultrasonicSensor = new UltrasonicSensor(SensorPort.S4);
		RegulatedMotor headMotor = Motor.C;
		float gearRatio = ControllableRobot.sensorGearRatio;
		scanner = new PhysicalRangeScanner(communicator, headMotor, ultrasonicSensor, gearRatio);

		// Infrared
		infrared = new PhysicalIRSensor(communicator, SensorPort.S3);

		// Command listener
		addMessageListener(this);

		// Start reporting updates
		startReporting();
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
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
	public RangeScanner getRangeScanner() {
		return scanner;
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public RangeFeatureDetector getRangeDetector() {
		return null;
	}

	@Override
	public IRSensor getIRSensor() {
		return infrared;
	}

	@Override
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public void addUpdateListener(RobotUpdateListener listener) {
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public void removeUpdateListener(RobotUpdateListener listener) {
	}

	/**
	 * Not implemented on NXT.
	 */
	@Override
	public CommandBuilder when(Condition condition) {
		return null;
	}

	@Override
	public void terminate() {
		// Stop reporting updates
		stopReporting();
		// Stop all communications
		getCommunicator().stop();
		// Release resources
		pilot.terminate();
		light.terminate();
		// Remove registered message listeners
		super.terminate();
	}

	@Override
	public void messageReceived(Command command) {
		if (command instanceof ShutdownCommand) {
			// Shut down
			terminate();
		}
	}

	/*
	 * Update reporting
	 */

	public void startReporting() {
		if (isRunning)
			return;

		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stopReporting() {
		isRunning = false;
		thread = null;
	}

	@Override
	public void run() {
		while (isRunning) {
			reportUpdate();
			Delay.msDelay(ControllableRobot.updateReportDelay);
		}
	}

	public void reportUpdate() {
		send(new UpdateReport(ReportType.UPDATE, RobotUpdate.create(this)));
	}

}
