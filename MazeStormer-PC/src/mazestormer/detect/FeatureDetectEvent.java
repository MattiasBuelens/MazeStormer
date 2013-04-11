package mazestormer.detect;

import static com.google.common.base.Preconditions.checkNotNull;
import lejos.robotics.objectdetection.Feature;
import mazestormer.player.Player;

public class FeatureDetectEvent {

	private final Player player;
	private final Feature feature;

	public FeatureDetectEvent(Player player, Feature feature) {
		this.player = checkNotNull(player);
		this.feature = checkNotNull(feature);
	}

	public Player getPlayer() {
		return player;
	}

	public Feature getFeature() {
		return feature;
	}

}
