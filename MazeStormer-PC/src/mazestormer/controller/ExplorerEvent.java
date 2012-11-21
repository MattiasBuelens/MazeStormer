package mazestormer.controller;


public class ExplorerEvent {

	public enum EventType {
		STARTED, STOPPED
	};

	private final EventType eventType;

	public ExplorerEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}
	
}
