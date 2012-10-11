package mazestormer.ui.map;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public class MapDocument {

	private SortedSet<MapLayer> layers = new TreeSet<MapLayer>();

	public void addLayer(MapLayer layer) {
		layers.add(new VisibleLayer(layer));
	}

	public void removeLayer(MapLayer layer) {
		layers.remove(layer);
	}

	public SVGDocument build() {
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		SVGDocument document = (SVGDocument) impl.createDocument(svgNS, "svg",
				null);
		Element root = document.getDocumentElement();

		for (MapLayer layer : layers) {
			Node layerNode = layer.build(document);
			if (layerNode != null) {
				root.appendChild(layerNode);
			}
		}

		return document;
	}

	private class VisibleLayer implements MapLayer {

		private final MapLayer layer;
		private boolean visible = true;
		private Node node;

		public VisibleLayer(MapLayer layer, boolean visible) {
			this.layer = layer;
			setVisible(visible);
		}

		public VisibleLayer(MapLayer layer) {
			this(layer, true);
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
			// TODO Trigger redraw
		}

		@Override
		public Node build(Document document) {
			node = layer.build(document);
			return node;
		}

		@Override
		public void render(SVGGraphics2D engine) {
			if (isVisible()) {
				layer.render(engine);
			}
		}

		@Override
		public int hashCode() {
			return layer.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return layer.equals(obj);
		}

		@Override
		public int getZIndex() {
			return layer.getZIndex();
		}

	}

}
