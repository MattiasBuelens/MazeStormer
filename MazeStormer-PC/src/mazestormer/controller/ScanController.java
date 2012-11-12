package mazestormer.controller;

import lejos.robotics.RangeScanner;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetectEvent;
import mazestormer.detect.RangeFeatureDetector;

public class ScanController extends SubController implements IScanController {

	public ScanController(MainController mainController) {
		super(mainController);
	}

	private boolean isConnected() {
		return getMainController().isConnected();
	}

	private RangeScanner getRangeScanner() {
		return getMainController().getRobot().getRangeScanner();
	}

	private RangeFeatureDetector getRangeDetector() {
		return getMainController().getRobot().getRangeDetector();
	}

	@Override
	public void scan(int range, int count) {
		if (!isConnected())
			return;

		// Get angles
		float[] angles = new float[count];
		float increment = range / (count - 1);
		float start = -range / 2f;
		for (int i = 0; i < count; i++) {
			angles[i] = start + i * increment;
		}
		// Configure scanner
		RangeScanner scanner = getRangeScanner();
		scanner.setAngles(angles);

		// Scan for readings
		RangeFeatureDetector detector = getRangeDetector();
		RangeFeature feature = detector.scan();

		// Publish
		if (feature != null) {
			postEvent(new RangeFeatureDetectEvent(feature));
		}
	}

}
