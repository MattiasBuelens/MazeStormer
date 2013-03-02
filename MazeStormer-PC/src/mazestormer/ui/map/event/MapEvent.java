package mazestormer.ui.map.event;

import mazestormer.player.IPlayer;

public abstract class MapEvent {

	private IPlayer player;

	protected MapEvent(IPlayer player) {
		this.player = player;
	}

	public IPlayer getPlayer() {
		return this.player;
	}

}
