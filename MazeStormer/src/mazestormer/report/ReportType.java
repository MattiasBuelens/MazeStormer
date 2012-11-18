package mazestormer.report;

import lejos.robotics.navigation.Move;
import mazestormer.remote.MessageType;

public enum ReportType implements MessageType<Report<?>> {

	/*
	 * Pilot
	 */

	MOVE_STARTED {
		@Override
		public Report<Move> build() {
			return new MoveReport(this);
		}
	},

	MOVE_STOPPED {
		@Override
		public Report<Move> build() {
			return new MoveReport(this);
		}
	},
	MOVEMENT {
		@Override
		public Report<Move> build() {
			return new MoveReport(this);
		}
	},

	/*
	 * Light sensor
	 */

	LIGHT_VALUE {
		@Override
		public Report<Integer> build() {
			return new LightReadReport(this);
		}
	};

}
