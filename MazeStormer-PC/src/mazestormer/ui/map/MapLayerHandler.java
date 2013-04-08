package mazestormer.ui.map;

public interface MapLayerHandler extends MapHandler {

	public void layerPropertyChanged(MapLayer layer, String propertyName, Object propertyValue);

}
