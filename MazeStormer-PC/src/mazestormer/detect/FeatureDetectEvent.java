package mazestormer.detect;

import static com.google.common.base.Preconditions.checkNotNull;

import lejos.robotics.objectdetection.Feature;

public class FeatureDetectEvent {

	private final Feature feature;

	public FeatureDetectEvent(Feature feature) {
		this.feature = checkNotNull(feature);
	}

	public Feature getFeature() {
		return feature;
	}

}
