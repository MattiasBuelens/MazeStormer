package mazestormer.map.event;

import mazestormer.ui.map.MapLayer;

public class MapLayerAddEvent {
	private final MapLayer layer;

	public MapLayerAddEvent(MapLayer layer) {
		this.layer = layer;
	}

	public MapLayer getLayer() {
		return layer;
	}
}
