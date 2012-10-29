package mazestormer;

import lejos.nxt.*;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class LineFollower {

	public static void main(String[] aArg) throws Exception {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		connector.setDeviceName("brons");
		connector.connect();

		Robot robot = connector.getRobot();
		LightSensor light = robot.getLightSensor();
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

		System.out.println("Current: " + light.readValue());
		double angle;
		while (true) {
			value = light.readValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				System.out.println(light.readValue());
				pilot.rotate(rotateAngle, false);
				pilot.setRotateSpeed(slowSpeed);
				pilot.rotateLeft();
				break;
			}

		}

		while (true) {
			value = light.readValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				System.out.println(light.readValue());
				pilot.setRotateSpeed(fastSpeed);
				pilot.rotate(-rotateAngle, false);
				pilot.setRotateSpeed(slowSpeed);
				pilot.rotateRight();
				break;
			}

		}

		while (true) {
			value = light.readValue();
			if (value > blackWhiteThreshold) {
				System.out.println("Found white: " + value);
				pilot.stop();
				angle = pilot.getMovement().getAngleTurned();
				System.out.println(light.readValue());
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