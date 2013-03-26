package mazestormer.ui.map.event;

import mazestormer.controller.IMapController;

import org.w3c.dom.svg.SVGDocument;

public class MapChangeEvent extends MapEvent {

	private final SVGDocument document;

	public MapChangeEvent(IMapController owner, SVGDocument document) {
		super(owner);
		this.document = document;
	}

	public SVGDocument getDocument() {
		return document;
	}

}
