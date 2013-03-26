package mazestormer.controller;

import java.util.Set;

import mazestormer.ui.map.MapLayer;
import mazestormer.util.EventSource;

import org.w3c.dom.svg.SVGDocument;

public interface IMapController extends EventSource {

	/**
	 * Get the map as a SVG document.
	 */
	public SVGDocument getDocument();

	/**
	 * Get the map layers to display.
	 */
	public Set<MapLayer> getLayers();

	/**
	 * Set the visibility of a map layer.
	 * 
	 * @param layer
	 *            The map layer.
	 * @param isVisible
	 *            Whether the layer should be visible.
	 */
	public void setLayerVisible(MapLayer layer, boolean isVisible);

	public void updatePoses();

}
