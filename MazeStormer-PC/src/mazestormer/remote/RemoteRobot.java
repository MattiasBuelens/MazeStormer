package mazestormer.remote;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import mazestormer.command.Command;
import mazestormer.report.Report;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class RemoteRobot implements Robot {

	private RemotePilot pilot;

	private final Communicator<Command, Report> communicator;

	public RemoteRobot(Communicator<Command, Report> communicator) {
		this.communicator = communicator;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RangeScanner getRangeScanner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PoseProvider getPoseProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public Communicator<Command, Report> getCommunicator() {
		return communicator;
	}

	public void setupCommunicator() {
		Communicator<Command, Report> comm = getCommunicator();

		// Report listeners

		// Commanders
	}

	@Override
	public void terminate() {
		getPilot().stop();
	}

}
