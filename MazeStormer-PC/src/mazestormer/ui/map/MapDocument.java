package mazestormer.ui.map;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

public class MapDocument {

	private SVGDocument document;

	private SortedMap<MapLayer, Element> layers = new TreeMap<MapLayer, Element>(
			new MapLayer.ZIndexComparator());

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
		layers.put(layer, null);
		buildLayers();
	}

	public void removeLayer(MapLayer layer) {
		layers.remove(layer);
		buildLayers();
	}

	protected void buildLayers() {
		SVGDocument document = getDocument();
		Element root = document.getDocumentElement();
		// Remove previous elements
		SVGUtils.removeChildNodes(root);
		// Add elements
		for (Map.Entry<MapLayer, Element> entry : layers.entrySet()) {
			Element layerElement = entry.getValue();
			if (layerElement == null) {
				// Create layer element
				MapLayer layer = entry.getKey();
				layerElement = layer.build((AbstractDocument) document);
				entry.setValue(layerElement);
			}
			root.appendChild(layerElement);
		}
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
