package mazestormer.robot;

import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTCommandConnector;
import lejos.robotics.navigation.DifferentialPilot;

public class PhysicalPilot extends DifferentialPilot implements Pilot {

	public PhysicalPilot(double wheelDiameter, double trackWidth) {
		this(wheelDiameter, wheelDiameter, trackWidth);
	}

	public PhysicalPilot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth) {
		super(leftWheelDiameter, rightWheelDiameter, trackWidth,
				getRemoteMotor(0), getRemoteMotor(1), false);
	}

	private static RemoteMotor getRemoteMotor(int id) {
		return new CachedRemoteMotor(NXTCommandConnector.getSingletonOpen(), id);
	}

}
