package mazestormer.map;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mazestormer.map.event.MapChangeEvent;
import mazestormer.map.event.MapLayerAddEvent;
import mazestormer.util.AbstractEventPublisher;
import mazestormer.util.EventPublisher;

import org.w3c.dom.svg.SVGDocument;

import com.google.common.eventbus.EventBus;

public class MapPanelController extends AbstractEventPublisher {

	private final MapPanel view;

	private MapDocument map;
	private List<MapLayer> layers = new ArrayList<MapLayer>();

	public MapPanelController(MapPanel view, EventBus eventBus) {
		this.map = new MapDocument();
		this.view = view;

		registerEventBus(eventBus);

		createMap();
		createLayers();
	}

	public MapPanelController(MapPanel view) {
		this(view, new EventBus());
	}

	@Override
	public void registerEventBus(EventBus eventBus) {
		super.registerEventBus(eventBus);

		// Propagate to models and views
		view.registerEventBus(eventBus);
		for (MapLayer layer : getLayers()) {
			layer.registerEventBus(eventBus);
		}
	}

	protected void postEvent(Object event) {
		if (getEventBus() != null)
			getEventBus().post(event);
	}

	private void createMap() {
		map.setViewRect(new Rectangle(-500, -500, 1000, 1000));

		SVGDocument document = map.getDocument();
		postEvent(new MapChangeEvent(document));
	}

	public Set<MapLayer> getLayers() {
		return map.getLayers();
	}

	private void addLayer(MapLayer layer) {
		layer.registerEventBus(getEventBus());
		layers.add(layer);
		map.addLayer(layer);
		postEvent(new MapLayerAddEvent(layer));
	}

	private void createLayers() {
		for (Layer layer : Layer.values()) {
			addLayer(layer.buildLayer());
		}
	}

	private enum Layer {
		Robot {
			@Override
			public MapLayer buildLayer() {
				return new RobotLayer("Robot");
			}
		};

		public abstract MapLayer buildLayer();
	}

}
