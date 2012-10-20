package mazestormer.robot;

import lejos.robotics.navigation.Move;

public class MoveEvent {
	public enum EventType {
		STARTED, STOPPED
	};

	private final EventType eventType;
	private final Move move;

	public MoveEvent(EventType type, Move move) {
		this.eventType = type;
		this.move = move;
	}

	public EventType getEventType() {
		return eventType;
	}

	public Move getMove() {
		return move;
	}

}
