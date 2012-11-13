package mazestormer.report;

import mazestormer.remote.MessageType;

public enum ReportType implements MessageType<Report> {

	/*
	 * Pilot movements
	 */
	MOVE_STARTED {
		@Override
		public Report build() {
			return new MoveReport(this);
		}
	},

	MOVE_STOPPED {
		@Override
		public Report build() {
			return new MoveReport(this);
		}
	};

}
