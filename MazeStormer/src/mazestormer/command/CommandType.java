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
	LIGHT_SET_LOW {
		@Override
		public Command build() {
			return new LightCalibrateCommand(this);
		}
	},
	LIGHT_SET_HIGH {
		@Override
		public Command build() {
			return new LightCalibrateCommand(this);
		}
	},

	/*
	 * Range scanner
	 */

	SCAN {
		@Override
		public Command build() {
			return new ScanCommand(this);
		}
	},

	/*
	 * Sound player
	 */

	PLAY_SOUND {
		@Override
		public Command build() {
			return new PlaySoundCommand(this);
		}
	},

	/*
	 * Conditional
	 */

	WHEN {
		@Override
		public Command build() {
			return new ConditionalCommand(this);
		}
	},

	CANCEL {
		@Override
		public Command build() {
			return new CancelRequestCommand(this);
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
