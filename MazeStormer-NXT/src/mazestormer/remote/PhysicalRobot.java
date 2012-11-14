package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

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
import mazestormer.command.ShutdownCommandListener;
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

	private List<MessageListener<Command>> messageListeners = new ArrayList<MessageListener<Command>>();

	public PhysicalRobot(Communicator<Report, Command> communicator) {
		this.communicator = communicator;
		setup();
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

	public void setup() {
		// Pilot
		pilot = new PhysicalPilot(communicator);

		// Light sensor
		light = new PhysicalLightSensor(communicator);

		// Scanner
		RangeFinder sensor = new UltrasonicSensor(SensorPort.S2);
		RegulatedMotor headMotor = Motor.C;
		scanner = new RotatingRangeScanner(headMotor, sensor);

		// Command listeners
		addMessageListener(new ShutdownCommandListener(this));

		// Reporters
	}

	@Override
	public void terminate() {
		// Stop all communications
		getCommunicator().stop();
		// Remove registered message listeners
		for (MessageListener<Command> listener : messageListeners) {
			communicator.removeListener(listener);
		}
		// Release resources
		getPilot().terminate();
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		messageListeners.add(listener);
		communicator.addListener(listener);
	}

}
