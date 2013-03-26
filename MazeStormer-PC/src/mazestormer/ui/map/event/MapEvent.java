package mazestormer.ui.map.event;

import mazestormer.controller.IMapController;

public abstract class MapEvent {

	private IMapController owner;

	protected MapEvent(IMapController owner) {
		this.owner = owner;
	}

	public IMapController getOwner() {
		return owner;
	}

}
