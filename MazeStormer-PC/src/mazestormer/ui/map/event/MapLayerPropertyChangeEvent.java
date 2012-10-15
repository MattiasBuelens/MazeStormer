package mazestormer.ui.map.event;

import mazestormer.ui.map.MapLayer;

public class MapLayerPropertyChangeEvent {

	private final MapLayer layer;
	private final String propertyName;
	private final Object propertyValue;

	public MapLayerPropertyChangeEvent(MapLayer layer, String propertyName,
			Object propertyValue) {
		this.layer = layer;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public MapLayer getLayer() {
		return layer;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

}
