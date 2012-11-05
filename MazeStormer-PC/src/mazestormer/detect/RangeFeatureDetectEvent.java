package mazestormer.detect;

import lejos.robotics.objectdetection.RangeFeature;

public class RangeFeatureDetectEvent extends FeatureDetectEvent {

	public RangeFeatureDetectEvent(RangeFeature feature) {
		super(feature);
	}

	public RangeFeature getFeature() {
		return (RangeFeature) super.getFeature();
	}

}
