package mazestormer.remote;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.Command;
import mazestormer.command.RotateCommandListener;
import mazestormer.command.ShutdownCommandListener;
import mazestormer.command.StopCommandListener;
import mazestormer.command.TravelCommandListener;
import mazestormer.report.MoveReporter;
import mazestormer.report.Report;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class PhysicalRobot implements Robot {

	private PhysicalPilot pilot;
	private PhysicalLightSensor light;
	private RangeScanner scanner;
	private PoseProvider poseProvider;

	private final Communicator<Report, Command> communicator;

	public PhysicalRobot(Communicator<Report, Command> communicator) {
		this.communicator = communicator;
		setupCommunicator();
	}

	@Override
	public Pilot getPilot() {
		if (pilot == null) {
			pilot = new PhysicalPilot(Robot.leftWheelDiameter,
					Robot.rightWheelDiameter, Robot.trackWidth, Motor.A,
					Motor.B, false);
		}
		return pilot;
	}

	@Override
	public CalibratedLightSensor getLightSensor() {
		if (light == null) {
			light = new PhysicalLightSensor(SensorPort.S1);
		}
		return light;
	}

	@Override
	public RangeScanner getRangeScanner() {
		if (scanner == null) {
			RangeFinder sensor = new UltrasonicSensor(SensorPort.S2);
			RegulatedMotor headMotor = Motor.C;
			scanner = new RotatingRangeScanner(headMotor, sensor);
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

	public Communicator<Report, Command> getCommunicator() {
		return communicator;
	}

	public void setupCommunicator() {
		Communicator<Report, Command> comm = getCommunicator();

		// Command listeners
		comm.addListener(new TravelCommandListener(this));
		comm.addListener(new RotateCommandListener(this));
		comm.addListener(new StopCommandListener(this));
		comm.addListener(new ShutdownCommandListener(this));

		// Reporters
		getPilot().addMoveListener(new MoveReporter(comm));
	}

	@Override
	public void terminate() {
		// Stop all communications
		getCommunicator().stop();
		// Release resources
		getPilot().terminate();
	}

}
