package mazestormer.ui.map.event;

import org.w3c.dom.svg.SVGDocument;

public class MapChangeEvent extends MapEvent {
	private final SVGDocument document;

	public MapChangeEvent(SVGDocument document, String playerID) {
		super(playerID);
		this.document = document;
	}

	public SVGDocument getDocument() {
		return document;
	}
}
