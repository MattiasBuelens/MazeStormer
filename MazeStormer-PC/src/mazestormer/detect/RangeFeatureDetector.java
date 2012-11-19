package mazestormer.detect;

import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.RangeFeature;

public interface RangeFeatureDetector extends FeatureDetector {

	@Override
	public RangeFeature scan();

}
