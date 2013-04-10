package mazestormer.remote;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.ScanCommand;
import mazestormer.detect.ObservableRangeScanner;
import mazestormer.report.RangeReadingReport;
import mazestormer.report.Report;
import mazestormer.report.ReportType;
import mazestormer.robot.RangeScannerListener;
import mazestormer.util.Future;
import mazestormer.util.ImmediateFuture;

public class PhysicalRangeScanner extends NXTComponent implements
		ObservableRangeScanner, RangeScannerListener {

	private final ObservableRangeScanner scanner;

	private final ScanReplier scanReplier;

	public PhysicalRangeScanner(NXTCommunicator communicator,
			ObservableRangeScanner scanner) {
		super(communicator);
		this.scanner = scanner;

		// Reply to scan commands
		scanReplier = new ScanReplier(getCommunicator());
		communicator.addListener(scanReplier);

		// Report readings
		addListener(this);
	}

	@Override
	public RangeReadings getRangeValues() {
		return scanner.getRangeValues();
	}

	@Override
	public Future<RangeReadings> getRangeValuesAsync() {
		return new ImmediateFuture<RangeReadings>(getRangeValues());
	}

	@Override
	public void setAngles(float[] angles) {
		scanner.setAngles(angles);
	}

	@Override
	public RangeFinder getRangeFinder() {
		return scanner.getRangeFinder();
	}

	@Override
	public void addListener(RangeScannerListener listener) {
		scanner.addListener(listener);
	}

	@Override
	public void removeListener(RangeScannerListener listener) {
		scanner.removeListener(listener);
	}

	/**
	 * Handles scan requests.
	 */
	private class ScanReplier extends CommandReplier<RangeReadings> {

		public ScanReplier(NXTCommunicator communicator) {
			super(communicator);
		}

		@Override
		public void messageReceived(Command command) {
			if (command instanceof ScanCommand) {
				onScanCommand((ScanCommand) command);
			}
		}

		private void onScanCommand(ScanCommand command) {
			// Scan at given angles
			float[] angles = command.getAngles();
			setAngles(angles);
			RangeReadings readings = getRangeValues();
			// Reply with readings
			reply(command, readings);
		}

		@Override
		protected MessageType<Report<?>> getResponseType(
				MessageType<Command> requestType) {
			return ReportType.SCAN;
		}
	}

	/**
	 * Report range readings.
	 */
	@Override
	public void readingReceived(RangeReading reading) {
		send(new RangeReadingReport(ReportType.RANGE_READING, reading));
	}

}
