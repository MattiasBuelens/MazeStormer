package mazestormer.controller;

import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.navigation.Pose;
import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.detect.RangeFeatureDetectEvent;

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

	private Pose getPose() {
		return getMainController().getPose();
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

		// Get readings
		RangeScanner scanner = getRangeScanner();
		scanner.setAngles(angles);
		RangeReadings readings = scanner.getRangeValues();

		// Publish readings
		RangeFeature feature = new RangeFeature(readings, getPose());
		postEvent(new RangeFeatureDetectEvent(feature));
	}

}
