package mazestormer.detect;

import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;

public class RangeTest {

	public static final String nxtName = "brons";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// Set up connection
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Remote);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		// Head
		RegulatedMotor head = Motor.C;
		head.setSpeed(600);

		// Range finder
		UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S2);
		sensor.continuous();

		float scanRange = 180;
		int scanCount = 6;

		float[] scanAngles = new float[scanCount];
		float scanIncrement = scanRange / (scanCount - 1);
		float scanStart = -scanRange / 2f;
		for (int i = 0; i < scanCount; i++) {
			scanAngles[i] = scanStart + i * scanIncrement;
		}

		// Range scanner
		RangeScanner scanner = new RotatingRangeScanner(head, sensor);
		scanner.setAngles(scanAngles);
		RangeReadings readings = scanner.getRangeValues();
		for (RangeReading reading : readings) {
			String msg = reading.getAngle() + "° = " + reading.getRange()
					+ " cm";
			System.out.println(msg);
		}
	}
}
