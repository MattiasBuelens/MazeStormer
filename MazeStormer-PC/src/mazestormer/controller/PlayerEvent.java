package mazestormer.controller;

import mazestormer.player.PlayerIdentifier;

public class PlayerEvent {

	public enum EventType {
		PLAYER_ADDED, PLAYER_REMOVED, PLAYER_RENAMED;
	}

	private final EventType eventType;
	private final PlayerIdentifier player;

	public PlayerEvent(EventType type, PlayerIdentifier player) {
		this.eventType = type;
		this.player = player;
	}

	public EventType getEventType() {
		return eventType;
	}

	public PlayerIdentifier getPlayer() {
		return player;
	}

}
