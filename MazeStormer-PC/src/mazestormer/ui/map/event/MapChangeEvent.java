package mazestormer.ui.map.event;

import mazestormer.player.IPlayer;

import org.w3c.dom.svg.SVGDocument;

public class MapChangeEvent extends MapEvent {

	private final SVGDocument document;

	public MapChangeEvent(SVGDocument document, IPlayer player) {
		super(player);
		this.document = document;
	}

	public SVGDocument getDocument() {
		return document;
	}

}
