package mazestormer.ui.map;

import java.awt.Rectangle;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

public class Map {

	private SVGDocument document;

	private SortedMap<MapLayer, Element> layers = new TreeMap<MapLayer, Element>(
			new MapLayer.ZIndexComparator());

	public void addLayer(MapLayer layer) {
		layers.put(layer, null);
		if (isBuilt())
			buildLayers();

	}

	public void removeLayer(MapLayer layer) {
		layers.remove(layer);
		if (isBuilt())
			buildLayers();
	}

	public void buildLayers() {
		SVGDocument document = getDocument();
		Element root = document.getDocumentElement();
		// Remove previous elements
		while (root.hasChildNodes()) {
			root.removeChild(root.getFirstChild());
		}
		// Add elements
		for (java.util.Map.Entry<MapLayer, Element> entry : layers.entrySet()) {
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
	 * Get or build the document.
	 * 
	 * @return The built document.
	 */
	public SVGDocument getDocument() {
		if (!isBuilt()) {
			document = buildDocument();
			buildLayers();
		}
		return document;
	}

	public boolean isBuilt() {
		return document != null;
	}

	/**
	 * Set the viewport rectangle of this document.
	 * 
	 * @param rect
	 */
	public void setViewRect(Rectangle rect) {
		SVGSVGElement svg = (SVGSVGElement) getDocument().getDocumentElement();
		SVGRect svgRect = svg.getViewBox().getBaseVal();
		svgRect.setX((float) rect.getX());
		svgRect.setY((float) rect.getY());
		svgRect.setWidth((float) rect.getWidth());
		svgRect.setHeight((float) rect.getHeight());
	}

	protected SVGDocument buildDocument() {
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		SVGDocument document = (SVGDocument) impl.createDocument(svgNS, "svg",
				null);
		return document;
	}

}
