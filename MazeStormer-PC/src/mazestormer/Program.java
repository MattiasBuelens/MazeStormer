package mazestormer;

import java.io.IOException;

import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.Robot;

public class Program {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// Set up command connection
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		connector.setDeviceName(nxtName);
		connector.connect();

		// Create robot
		Robot robot = connector.getRobot();
		float travelSpeed = 15; // cm/sec
		float rotateSpeed = 90; // degrees/sec
		// int acceleration = (int) (travelSpeed * 0.5); // cm/sec²

		// robot.setAcceleration(acceleration);
		robot.setRotateSpeed(rotateSpeed);
		robot.setTravelSpeed(travelSpeed);

		// Travel along a polygon
		int nSides = 5;
		int direction = 1; // 1 = counterclockwise, -1 = clockwise
		double sideLength = 50d;
		for (int i = 0; i < nSides; ++i) {
			robot.travel(sideLength);
			robot.rotate(direction * 360d / (double) nSides);
		}
		robot.stop();

		connector.disconnect();
	}

}
