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
import mazestormer.detect.RangeScannerFeatureDetector;
import mazestormer.detect.ReadingAngleComparator;
import mazestormer.robot.Pilot;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

public class ScanTest implements FeatureListener {

	/**
	 * The name of the NXT to connect to.
	 */
	public static final String nxtName = "brons";

	private boolean isRunning;

	private void start() {
		Connector connector = new ConnectionProvider()
				.getConnector(RobotType.Physical);
		ConnectionContext context = new ConnectionContext();
		context.setDeviceName("brons");
		connector.connect(context);

		Pilot pilot = connector.getRobot().getPilot();
		PoseProvider pp = new OdometryPoseProvider(pilot);

		RangeScanner scanner = connector.getRobot().getRangeScanner();

		float scanRange = 360f;
		float scanIncrement = 5f;

		int scanCount = (int) (scanRange / scanIncrement) + 1;
		float[] scanAngles = new float[scanCount];
		float scanStart = -scanRange / 2f;
		for (int i = 0; i < scanCount; i++) {
			scanAngles[i] = scanStart + i * scanIncrement;
		}
		scanner.setAngles(scanAngles);

		float maxDistance = 255f;
		int delay = 10 * 1000;
		RangeScannerFeatureDetector detector = new RangeScannerFeatureDetector(
				scanner, maxDistance);
		detector.setDelay(delay);
		detector.setPoseProvider(pp);
		detector.addListener(this);
		detector.enableDetection(true);

		isRunning = true;
		while (isRunning)
			Thread.yield();
		detector.enableDetection(false);
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new ScanTest().start();
	}

	@Override
	public void featureDetected(Feature feature, FeatureDetector detector) {
		if (feature == null)
			return;
		RangeFeature rangeFeature = (RangeFeature) feature;

		List<RangeReading> readings = Ordering.from(
				new ReadingAngleComparator()).sortedCopy(
				rangeFeature.getRangeReadings());

		for (RangeReading reading : readings) {
			float angle = reading.getAngle();
			float range = reading.getRange();
			Point point = rangeFeature.getPose().pointAt(range, angle);
			System.out.println(Joiner.on(';').join(angle, range, point.getX(),
					point.getY()));
		}

		isRunning = false;
	}
}
