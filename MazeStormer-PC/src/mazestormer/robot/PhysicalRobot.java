package mazestormer.robot;

import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTCommandConnector;
import lejos.robotics.navigation.DifferentialPilot;

public class PhysicalRobot extends DifferentialPilot implements Robot {

	public PhysicalRobot(double wheelDiameter, double trackWidth) {
		this(wheelDiameter, wheelDiameter, trackWidth);
	}

	public PhysicalRobot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth) {
		super(leftWheelDiameter, rightWheelDiameter, trackWidth,
				getRemoteMotor(0), getRemoteMotor(1), false);
	}

	private static RemoteMotor getRemoteMotor(int id) {
		return new CachedRemoteMotor(NXTCommandConnector.getSingletonOpen(), id);
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

}
