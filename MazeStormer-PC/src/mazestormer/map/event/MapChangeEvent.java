package mazestormer.map.event;

import org.w3c.dom.svg.SVGDocument;

public class MapChangeEvent {
	private final SVGDocument document;

	public MapChangeEvent(SVGDocument document) {
		this.document = document;
	}

	public SVGDocument getDocument() {
		return document;
	}
}
