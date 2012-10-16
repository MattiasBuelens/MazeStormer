package mazestormer.robot;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class PhysicalRobot extends DifferentialPilot implements Robot {

	public PhysicalRobot(double wheelDiameter, double trackWidth) {
		this(wheelDiameter, wheelDiameter, trackWidth);
	}

	public PhysicalRobot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth) {
		super(leftWheelDiameter, rightWheelDiameter, trackWidth, Motor.A,
				Motor.B, false);
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
