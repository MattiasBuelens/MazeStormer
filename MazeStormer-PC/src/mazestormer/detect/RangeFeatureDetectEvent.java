package mazestormer.detect;

import lejos.robotics.objectdetection.RangeFeature;
import mazestormer.player.Player;

public class RangeFeatureDetectEvent extends FeatureDetectEvent {

	public RangeFeatureDetectEvent(Player player, RangeFeature feature) {
		super(player, feature);
	}

	@Override
	public RangeFeature getFeature() {
		return (RangeFeature) super.getFeature();
	}

}
