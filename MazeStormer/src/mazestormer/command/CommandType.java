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
			// TODO Auto-generated method stub
			return null;
		}
	},
	SET_ROTATE_SPEED {
		@Override
		public Command build() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	SET_ACCELERATION {
		@Override
		public Command build() {
			// TODO Auto-generated method stub
			return null;
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
