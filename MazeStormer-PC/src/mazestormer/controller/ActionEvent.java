package mazestormer.controller;

public abstract class ActionEvent {

	public enum EventType {
		STARTED, STOPPED;
	}

	private final EventType eventType;

	public ActionEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}

}
