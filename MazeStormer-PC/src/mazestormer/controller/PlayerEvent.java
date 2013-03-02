package mazestormer.controller;

import mazestormer.player.IPlayer;

public class PlayerEvent {

	public enum EventType {
		PLAYER_ADDED, PLAYER_REMOVED, PLAYER_RENAMED;
	}

	private final EventType eventType;
	private final IPlayer player;

	public PlayerEvent(EventType type, IPlayer player) {
		this.eventType = type;
		this.player = player;
	}

	public EventType getEventType() {
		return eventType;
	}

	public IPlayer getPlayer() {
		return player;
	}

}
