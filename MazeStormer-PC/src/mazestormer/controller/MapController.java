package mazestormer.controller;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mazestormer.ui.map.MapDocument;
import mazestormer.ui.map.MapLayer;
import mazestormer.ui.map.RobotLayer;
import mazestormer.ui.map.event.MapChangeEvent;
import mazestormer.ui.map.event.MapLayerAddEvent;

import org.w3c.dom.svg.SVGDocument;

public class MapController extends SubController implements IMapController {

	private MapDocument map;
	private List<MapLayer> layers = new ArrayList<MapLayer>();

	public MapController(MainController mainController) {
		super(mainController);

		createMap();
		createLayers();
	}

	private void createMap() {
		map = new MapDocument();

		// TODO Make maze define the view rectangle
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));

		SVGDocument document = map.getDocument();
		postEvent(new MapChangeEvent(document));
	}

	private void createLayers() {
		addLayer(new RobotLayer("Robot"));
	}

	private void addLayer(MapLayer layer) {
		layer.registerEventBus(getEventBus());
		layers.add(layer);
		map.addLayer(layer);
		postEvent(new MapLayerAddEvent(layer));
	}

	@Override
	public SVGDocument getDocument() {
		return map.getDocument();
	}

	@Override
	public Set<MapLayer> getLayers() {
		return map.getLayers();
	}

	@Override
	public void setLayerVisible(MapLayer layer, boolean isVisible) {
		layer.setVisible(isVisible);
	}

}
