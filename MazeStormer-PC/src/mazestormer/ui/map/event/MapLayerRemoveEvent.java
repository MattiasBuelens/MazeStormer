package mazestormer.ui.map.event;

import mazestormer.controller.IMapController;
import mazestormer.ui.map.MapLayer;

public class MapLayerRemoveEvent extends MapEvent {

	private final MapLayer layer;

	public MapLayerRemoveEvent(IMapController owner, MapLayer layer) {
		super(owner);
		this.layer = layer;
	}

	public MapLayer getLayer() {
		return layer;
	}

}
