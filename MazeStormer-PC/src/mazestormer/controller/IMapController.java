package mazestormer.controller;

import java.util.Set;

import org.w3c.dom.svg.SVGDocument;

import lejos.robotics.navigation.Pose;
import mazestormer.ui.map.MapLayer;
import mazestormer.util.EventSource;

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
	 * 			The map layer.
	 * @param isVisible
	 * 			Whether the layer should be visible.
	 */
	public void setLayerVisible(MapLayer layer, boolean isVisible);

	/**
	 * Get the robot's current pose, in map coordinates.
	 */
	public Pose getRobotPose();

}
