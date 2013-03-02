package mazestormer.ui.map.event;

import mazestormer.player.IPlayer;
import mazestormer.ui.map.MapLayer;

public class MapLayerAddEvent extends MapEvent {

	private final MapLayer layer;

	public MapLayerAddEvent(MapLayer layer, IPlayer player) {
		super(player);
		this.layer = layer;
	}

	public MapLayer getLayer() {
		return layer;
	}

}
