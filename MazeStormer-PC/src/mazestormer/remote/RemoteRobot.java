package mazestormer.remote;

import lejos.robotics.RangeScanner;
import lejos.robotics.localization.PoseProvider;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class RemoteRobot implements Robot {

	private final RemoteCommunicator communicator;

	public RemoteRobot(RemoteCommunicator communicator) {
		this.communicator = communicator;
	}

	@Override
	public Pilot getPilot() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void terminate() {
		getPilot().stop();
	}

}
