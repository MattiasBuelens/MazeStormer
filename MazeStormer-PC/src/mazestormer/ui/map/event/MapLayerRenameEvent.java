package mazestormer.ui.map.event;

import mazestormer.controller.IMapController;
import mazestormer.ui.map.MapLayer;

public class MapLayerRenameEvent extends MapEvent {

	private final MapLayer layer;

	public MapLayerRenameEvent(IMapController owner, MapLayer layer) {
		super(owner);
		this.layer = layer;
	}

	public MapLayer getLayer() {
		return layer;
	}

}