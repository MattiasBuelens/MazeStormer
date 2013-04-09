package mazestormer.report;

import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.Move;
import mazestormer.remote.MessageType;
import mazestormer.robot.RobotUpdate;

public enum ReportType implements MessageType<Report<?>> {

	/*
	 * General
	 */
	UPDATE {
		@Override
		public Report<RobotUpdate> build() {
			return new UpdateReport(this);
		}
	},

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

	/*
	 * Light sensor
	 */

	LIGHT_VALUE {
		@Override
		public Report<Integer> build() {
			return new LightReadReport(this);
		}
	},

	/*
	 * IR sensor
	 */

	IR_VALUE {
		@Override
		public Report<float[]> build() {
			return new IRReadReport(this);
		}
	},

	/*
	 * Ultrasonic sensor
	 */

	SCAN {
		@Override
		public Report<RangeReadings> build() {
			return new ScanReport(this);
		}
	},

	/*
	 * Conditions
	 */

	CONDITION_RESOLVED {
		@Override
		public Report<Void> build() {
			return new ConditionReport(this);
		}
	};

}
