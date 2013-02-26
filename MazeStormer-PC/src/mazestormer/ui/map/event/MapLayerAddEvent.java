package mazestormer.ui.map.event;

import mazestormer.ui.map.MapLayer;

public class MapLayerAddEvent extends MapEvent {
	private final MapLayer layer;

	public MapLayerAddEvent(MapLayer layer, String playerID) {
		super(playerID);
		this.layer = layer;
	}

	public MapLayer getLayer() {
		return layer;
	}
}
