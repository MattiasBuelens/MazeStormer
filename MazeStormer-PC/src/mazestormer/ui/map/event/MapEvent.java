package mazestormer.ui.map.event;

import mazestormer.player.PlayerIdentifier;

public abstract class MapEvent {

	private PlayerIdentifier player;

	protected MapEvent(PlayerIdentifier player) {
		this.player = player;
	}

	public PlayerIdentifier getPlayer() {
		return this.player;
	}

}
