package mazestormer;

import java.io.IOException;
import java.util.List;

import lejos.geom.Point;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.connect.ConnectionContext;
import mazestormer.connect.ConnectionProvider;
import mazestormer.connect.Connector;
import mazestormer.connect.RobotType;
import mazestormer.detect.ReadingAngleComparator;
import mazestormer.robot.Pilot;

import com.google.common.collect.Ordering;

public class InitialTest implements FeatureListener {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	private boolean isRunning;

	private float scanRange = 360f; // °
	private float scanIncrement = 5f; // °
	private float maxDistance = 255f; // cm
	private int delay = 10 * 1000; // ms

	private Connector connector;

	private void start() {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Remote);

		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		Pilot pilot = connector.getRobot().getPilot();
		PoseProvider pp = new OdometryPoseProvider(pilot);

		RangeScanner scanner = connector.getRobot().getRangeScanner();

		// 1) scan 360°

		// int scanCount = (int) (scanRange / scanIncrement) + 1;
		// float[] scanAngles = new float[scanCount];
		// float scanStart = -scanRange / 2f;
		// for (int i = 0; i < scanCount; i++) {
		// scanAngles[i] = scanStart + i * scanIncrement;
		// }
		// scanner.setAngles(scanAngles);
		//
		// RangeScannerFeatureDetector detector = new
		// RangeScannerFeatureDetector(
		// scanner, maxDistance, delay);
		// detector.setPoseProvider(pp);
		// detector.addListener(this);
		// detector.enableDetection(true);

		isRunning = true;
		while (isRunning)
			Thread.yield();
		// detector.enableDetection(false);
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new InitialTest().start();
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		if (feature == null)
			return;
		RangeFeature rangeFeature = (RangeFeature) feature;

		List<RangeReading> readings = Ordering.from(
				new ReadingAngleComparator()).sortedCopy(
				rangeFeature.getRangeReadings());

		// 2) bereken zwaartepunt van data
		float sumX = 0f;
		float sumY = 0f;
		Point centerOfMass;

		for (RangeReading reading : readings) {
			float angle = reading.getAngle();
			float range = reading.getRange();
			Point point = rangeFeature.getPose().pointAt(range, angle);
			sumX = (float) (sumX + point.getX());
			sumY = (float) (sumY + point.getY());
		}

		centerOfMass = new Point(sumX / readings.size(), sumY / readings.size());

		// 3) bepaal datapunt met maximale afstand tot G (heeft richting alfa
		// tov oorsprong)
		float MaxDistance = 0;
		double distance;
		RangeReading chosenReading = null;

		for (RangeReading reading : readings) {
			float angle = reading.getAngle();
			float range = reading.getRange();
			Point point = rangeFeature.getPose().pointAt(range, angle);
			distance = point.distance(centerOfMass);
			if (distance > MaxDistance)
				chosenReading = reading;
		}

		// 4) draai in de gevonden richting en rijd tot witte lijn wordt gezien
		if (chosenReading == null)
			System.out.println("Chosen Reading is null.");

		System.out.println("Connector: " + connector);
		System.out.println("Robot: " + connector.getRobot());
		System.out.println("Pilot: " + connector.getRobot().getPilot());
		// connector.getRobot().getPilot().rotate(chosenReading.getAngle());

		// 5) rijd tot witte lijn wordt gezien
		// 6) positioneer loodrecht op witte lijn
		// 7) rijd 20cm achteruit
		// 8) scan op 90° (links) en op -90° (rechts) hou rekening met verschil
		// tss centrum robot en centrum scanner
		// 9) beoordeel resultaten: >40 => onbruikbaar
		// 10) uit resterende resultaten: bepaal hoek (beta) en afstand (delta)
		// om te rijden naar centrum van tegel
		// 11) draai beta, rij delta, draai -beta

		isRunning = false;
	}
}
