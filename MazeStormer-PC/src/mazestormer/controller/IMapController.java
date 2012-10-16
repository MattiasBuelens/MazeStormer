package mazestormer.controller;

import java.util.Set;

import org.w3c.dom.svg.SVGDocument;

import mazestormer.ui.map.MapLayer;
import mazestormer.util.EventSource;

public interface IMapController extends EventSource {

	public SVGDocument getDocument();

	public Set<MapLayer> getLayers();

	public void setLayerVisible(MapLayer layer, boolean isVisible);

}
