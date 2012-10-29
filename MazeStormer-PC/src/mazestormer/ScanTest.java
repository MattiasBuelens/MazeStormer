package mazestormer;

import java.io.IOException;

import lejos.robotics.RangeReading;
import lejos.robotics.RangeScanner;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;

public class ScanTest {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		connector.setDeviceName(nxtName);
		connector.connect();

		RangeScanner scanner = connector.getRobot().getRangeScanner();

		float[] angles = { -90f, -75f, -60f, -45f, -30f, -15f, 0, 15f, 30f,
				45f, 60f, 75f, 90f };
		scanner.setAngles(angles);

		for (RangeReading reading : scanner.getRangeValues()) {
			String msg = reading.getAngle() + "° = " + reading.getRange();
			System.out.println(msg);
		}

	}

}
