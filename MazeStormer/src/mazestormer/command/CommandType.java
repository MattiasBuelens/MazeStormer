package mazestormer.command;

public enum CommandType {

	/**
	 * Pilot commands
	 */
	TRAVEL, ROTATE, STOP,

	/**
	 * Pilot configuration
	 */
	SET_TRAVEL_SPEED, SET_ROTATE_SPEED, SET_ACCELERATION;

}
