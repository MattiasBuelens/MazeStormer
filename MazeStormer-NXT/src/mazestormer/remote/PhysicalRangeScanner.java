package mazestormer.remote;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.ScanCommand;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class PhysicalRangeScanner extends CommandReplier<RangeReadings>
		implements RangeScanner {

	private final RangeScanner scanner;

	public PhysicalRangeScanner(NXTCommunicator communicator,
			RangeScanner scanner) {
		super(communicator);
		communicator.addListener(this);

		this.scanner = scanner;
	}

	@Override
	public RangeReadings getRangeValues() {
		return scanner.getRangeValues();
	}

	@Override
	public void setAngles(float[] angles) {
		scanner.setAngles(angles);
	}

	@Override
	public RangeFinder getRangeFinder() {
		return scanner.getRangeFinder();
	}

	/**
	 * Handles scan requests.
	 */
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
