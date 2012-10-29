package mazestormer.robot;

import lejos.robotics.navigation.DifferentialPilot;

public class PhysicalPilot extends DifferentialPilot implements Pilot {

	public PhysicalPilot(double wheelDiameter, double trackWidth) {
		this(wheelDiameter, wheelDiameter, trackWidth);
	}

	public PhysicalPilot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth) {
		super(leftWheelDiameter, rightWheelDiameter, trackWidth,
				CachedRemoteMotor.get(0), CachedRemoteMotor.get(1), false);
	}

}
