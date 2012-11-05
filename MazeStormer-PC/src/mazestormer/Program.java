package mazestormer;

import java.io.IOException;

import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.robot.Pilot;

public class Program {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// Set up connection
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		// Create pilot
		Pilot pilot = connector.getRobot().getPilot();
		float travelSpeed = 15; // cm/sec
		float rotateSpeed = 90; // degrees/sec
		// int acceleration = (int) (travelSpeed * 0.5); // cm/sec²

		// pilot.setAcceleration(acceleration);
		pilot.setRotateSpeed(rotateSpeed);
		pilot.setTravelSpeed(travelSpeed);

		// Travel along a polygon
		int nSides = 5;
		int direction = 1; // 1 = counterclockwise, -1 = clockwise
		double sideLength = 50d;
		for (int i = 0; i < nSides; ++i) {
			pilot.travel(sideLength);
			pilot.rotate(direction * 360d / (double) nSides);
		}
		pilot.stop();

		connector.disconnect();
	}

}
