package mazestormer.controller;

public class PolygonEvent {

	public enum EventType {
		STARTED, STOPPED
	};

	private final EventType eventType;

	public PolygonEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}

}
