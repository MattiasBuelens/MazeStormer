package mazestormer.controller;

public class GameEvent {
	
	public enum EventType {
		PLAYER_ADDED, PLAYER_REMOVED;
	}

	private final EventType eventType;

	public GameEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}
}
