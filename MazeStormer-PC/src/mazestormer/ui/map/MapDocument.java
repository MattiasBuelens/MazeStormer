package mazestormer.ui.map;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

public class MapDocument extends MapElement {

	private SVGDocument document;
	private Map<MapLayer, Element> layers = new HashMap<MapLayer, Element>();

	public MapDocument() {
		document = SVGUtils.createSVGDocument();
	}

	/**
	 * Gets the document.
	 * 
	 * @return The document.
	 */
	public SVGDocument getDocument() {
		return document;
	}

	public Set<MapLayer> getLayers() {
		return Collections.unmodifiableSet(layers.keySet());
	}

	public void addLayer(MapLayer layer) {
		if (layer instanceof RobotLayer) {
			System.out.println("Add robot layer: " + layer.getName());
		}
		layers.put(layer, null);
		invokeBuildLayers();
	}

	public void removeLayer(MapLayer layer) {
		layers.remove(layer);
		invokeBuildLayers();
	}

	protected void buildLayers() {
		SVGDocument document = getDocument();
		Element root = document.getDocumentElement();
		// Remove previous elements
		SVGUtils.removeChildNodes(root);
		// Sort layers on z-index
		MapLayer[] sortedLayers = layers.keySet().toArray(new MapLayer[0]);
		Arrays.sort(sortedLayers, new MapLayer.ZIndexComparator());
		// Add elements
		for (MapLayer layer : sortedLayers) {
			Element layerElement = layers.get(layer);
			if (layerElement == null) {
				// Create layer element
				layerElement = layer.build((AbstractDocument) document);
				layers.put(layer, layerElement);
			}
			root.appendChild(layerElement);
		}
	}

	protected void invokeBuildLayers() {
		invokeDOMChange(new Runnable() {
			@Override
			public void run() {
				buildLayers();
			}
		});
	}

	/**
	 * Sets the viewport rectangle of this document.
	 * 
	 * @param rect
	 *            The new viewport rectangle.
	 */
	public void setViewRect(Rectangle rect) {
		SVGSVGElement svg = (SVGSVGElement) getDocument().getDocumentElement();
		SVGRect svgRect = svg.getViewBox().getBaseVal();
		svgRect.setX((float) rect.getX());
		svgRect.setY((float) rect.getY());
		svgRect.setWidth((float) rect.getWidth());
		svgRect.setHeight((float) rect.getHeight());
	}

}
