package mazestormer.controller;

public class ConfigurationEvent {

	public enum EventType {
		NEW_MAZE_LOADED;
	}

	private final EventType eventType;

	public ConfigurationEvent(EventType type) {
		this.eventType = type;
	}

	public EventType getEventType() {
		return eventType;
	}
}
