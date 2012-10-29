package mazestormer.controller;

public class ActionEvent{

	private final EventType eventType;

	public ActionEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}
}
