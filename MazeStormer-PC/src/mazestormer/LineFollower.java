package mazestormer;

import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.ControllableRobot;

public class LineFollower {

	public static void main(String[] aArg) throws Exception {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.PHYSICAL);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		ControllableRobot robot = connector.getRobot();
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		light.setFloodlight(true);

		// TODO: name?
		final int blackWhiteThreshold = 30;

		double slowSpeed, fastSpeed;
		slowSpeed = 20;
		fastSpeed = 50;

		double rotateAngle = 180.0;

		light.setLow(388);
		light.setHigh(428);

		pilot.setTravelSpeed(5);
		pilot.setRotateSpeed(fastSpeed);
		pilot.forward();

		int value;

		System.out.println("Current: " + light.getLightValue());
		double angle;
		while (true) {
			value = light.getLightValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				System.out.println(light.getLightValue());
				pilot.rotate(rotateAngle, false);
				pilot.setRotateSpeed(slowSpeed);
				pilot.rotateLeft();
				break;
			}

		}

		while (true) {
			value = light.getLightValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				System.out.println(light.getLightValue());
				pilot.setRotateSpeed(fastSpeed);
				pilot.rotate(-rotateAngle, false);
				pilot.setRotateSpeed(slowSpeed);
				pilot.rotateRight();
				break;
			}

		}

		while (true) {
			value = light.getLightValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				angle = pilot.getMovement().getAngleTurned();
				System.out.println(light.getLightValue());
				break;
			}

		}

		angle = Math.abs(angle) + rotateAngle;

		System.out.println("Angle: " + angle);

		boolean groter = false;

		// TODO: Hoek is altijd groter dan 180 graden nu
		if (angle > 180) {
			groter = true;
			angle = angle - 360;
		}

		double extra = 0;
		if (groter) {
			extra = 3;
			System.out.println("Extra!");
		}

		pilot.setRotateSpeed(fastSpeed);
		pilot.rotate((angle / 2.0) - extra);

		double dist = 7.2 * Math.cos(Math.toRadians(angle / 2.0));
		pilot.travel(dist);
		System.out.println(dist);

	}
}