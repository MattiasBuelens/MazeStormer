package mazestormer.controller;

public class GameSetUpEvent {

	public enum EventType {
		JOINED, LEFT, DISCONNECTED, NOT_READY;
	}

	private final EventType eventType;

	public GameSetUpEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}
}
