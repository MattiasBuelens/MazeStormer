package mazestormer.controller;

public class PolygonEvent{

	private final EventType eventType;

	public PolygonEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}

}
