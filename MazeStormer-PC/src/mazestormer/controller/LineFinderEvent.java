package mazestormer.controller;


public class LineFinderEvent {

	public enum EventType {
		STARTED, STOPPED
	};

	private final EventType eventType;

	public LineFinderEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}

}
