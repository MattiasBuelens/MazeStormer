package mazestormer;

import java.io.IOException;

import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class NXTLineFinder {

	public static void main(String[] args) throws IOException,
			InterruptedException {


		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		Robot robot = connector.getRobot();
		CalibratedLightSensor light = robot.getLightSensor();
		Pilot pilot = robot.getPilot();
		light.setFloodlight(true);

		light.setHigh(585);
		light.setLow(507);
		
		double maxAttackAngle = 20.0;
		double safetyAngle = 10.0;
		double fastRotateAngle = -(90 - maxAttackAngle - safetyAngle);
		
		final int threshold = 50;

		double slowRotateSpeed, fastRotateSpeed;
		slowRotateSpeed = 20;
		fastRotateSpeed = 50;

		double slowSpeed, fastSpeed;
		slowSpeed = 1.0;
		fastSpeed = 5.0;
		
		// TODO: Speed?
		pilot.setTravelSpeed(fastSpeed);

		// Start looking for line

		pilot.forward();

		int value;

		double angle1 = 0, angle2 = 0;

		while (true) {
			value = light.getLightValue();
			if (value > threshold) {
				// Found line
				pilot.stop();
				pilot.setTravelSpeed(slowSpeed);
				pilot.forward();
				break;
			}

		}

		while (true) {
			value = light.getLightValue();
			if (value < threshold) {
				// Over white line
				pilot.stop();
				pilot.setTravelSpeed(fastSpeed);
				pilot.travel(Robot.sensorOffset - 1.4,false);
				
				pilot.setRotateSpeed(fastRotateSpeed);
				pilot.rotate(fastRotateAngle, false);
				pilot.setRotateSpeed(slowRotateSpeed);
				
				pilot.rotateRight();
				break;
			}
		}

		while (true) {
			value = light.getLightValue();
			if (value > threshold) {
				pilot.stop();
				break;
			}
		}
		pilot.setRotateSpeed(fastRotateSpeed);
		pilot.rotate(90.0);
		//pilot.travel(20.0 - 0.9);
//		angle1 = Math.abs(angle1) + rotateAngle;
//		angle2 = Math.abs(angle2) + rotateAngle;
//
//		pilot.setRotateSpeed(fastRotateSpeed);
//
//		double finalAngle;
//
//		finalAngle = ((angle2 - 360.0) / 2.0) - extraAngle;
//
//		pilot.rotate(finalAngle);
//		double dist = Robot.sensorOffset * Math.cos(Math.toRadians(finalAngle));
//
//		pilot.travel(dist);

	}
}
