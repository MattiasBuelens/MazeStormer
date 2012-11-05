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

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

public class InitialTest implements FeatureListener {

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

		// initieel algoritme:
		// scan 360°
		// bereken zwaartepunt van data (G)
		// bepaal datapunt met maximale afstand tot G (heeft richting alfa tov
		// oorsprong)
		// draai alfa
		// rijd tot witte lijn wordt gezien
		// positioneer loodrecht op witte lijn
		// rijd 20cm achteruit
		// scan op 90° (links) en op -90° (rechts)
		// beoordeel resultaten: >40 => onbruikbaar
		// uit resterende resultaten: bepaal hoek (beta) en afstand (delta) om
		// te rijden naar centrum van tegel
		// draai beta, rij delta, draai -beta
		// einde
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
