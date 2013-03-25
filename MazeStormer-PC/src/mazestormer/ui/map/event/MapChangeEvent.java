package mazestormer.ui.map.event;

import mazestormer.player.PlayerIdentifier;

import org.w3c.dom.svg.SVGDocument;

public class MapChangeEvent extends MapEvent {

	private final SVGDocument document;

	public MapChangeEvent(SVGDocument document, PlayerIdentifier player) {
		super(player);
		this.document = document;
	}

	public SVGDocument getDocument() {
		return document;
	}

}
