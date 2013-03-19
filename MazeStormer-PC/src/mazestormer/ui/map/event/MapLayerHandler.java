package mazestormer.ui.map.event;

import mazestormer.ui.map.MapLayer;

public interface MapLayerHandler {

	public void requestDOMChange(Runnable request);

	public void layerPropertyChanged(MapLayer layer, String propertyName, Object propertyValue);

}
