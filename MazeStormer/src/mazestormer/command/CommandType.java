package mazestormer.command;

import mazestormer.remote.MessageType;

public enum CommandType implements MessageType<Command> {

	/*
	 * Pilot commands
	 */

	TRAVEL {
		@Override
		public Command build() {
			return new TravelCommand(this);
		}
	},
	ROTATE {
		@Override
		public Command build() {
			return new RotateCommand(this);
		}

	},
	STOP {
		@Override
		public Command build() {
			return new StopCommand(this);
		}
	},

	/*
	 * Pilot configuration
	 */

	SET_TRAVEL_SPEED {
		@Override
		public Command build() {
			return new PilotParameterCommand(this);
		}
	},
	SET_ROTATE_SPEED {
		@Override
		public Command build() {
			return new PilotParameterCommand(this);
		}
	},
	SET_ACCELERATION {
		@Override
		public Command build() {
			return new PilotParameterCommand(this);
		}
	},

	/*
	 * Light sensor
	 */
	LIGHT_READ {
		@Override
		public Command build() {
			return new LightReadCommand(this);
		}
	},
	LIGHT_FLOODLIGHT {
		@Override
		public Command build() {
			return new LightFloodlightCommand(this);
		}
	},

	/*
	 * General
	 */

	SHUTDOWN {
		@Override
		public Command build() {
			return new ShutdownCommand(this);
		}
	};

}
