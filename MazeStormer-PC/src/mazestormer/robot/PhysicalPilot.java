package mazestormer.robot;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class PhysicalPilot extends DifferentialPilot implements Pilot {

	public PhysicalPilot(double wheelDiameter, double trackWidth,
			RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
		this(wheelDiameter, trackWidth, leftMotor, rightMotor, false);
	}

	public PhysicalPilot(double wheelDiameter, double trackWidth,
			RegulatedMotor leftMotor, RegulatedMotor rightMotor, boolean reverse) {
		this(wheelDiameter, wheelDiameter, trackWidth, leftMotor, rightMotor,
				reverse);
	}

	public PhysicalPilot(double leftWheelDiameter, double rightWheelDiameter,
			double trackWidth, RegulatedMotor leftMotor,
			RegulatedMotor rightMotor, boolean reverse) {
		super(leftWheelDiameter, rightWheelDiameter, trackWidth, leftMotor,
				rightMotor, reverse);
	}

	@Override
	public void terminate() {
		stop();
	}

}
