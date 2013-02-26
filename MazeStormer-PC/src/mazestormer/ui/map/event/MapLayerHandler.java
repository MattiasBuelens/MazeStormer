package mazestormer.ui.map.event;

import mazestormer.ui.map.MapLayer;

public interface MapLayerHandler {

	void requestDOMChange(Runnable request);

	void layerPropertyChanged(MapLayer layer, String propertyName,
			Object propertyValue);

}
