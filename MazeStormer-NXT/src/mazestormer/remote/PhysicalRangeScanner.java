package mazestormer.remote;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RotatingRangeScanner;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.ScanCommand;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class PhysicalRangeScanner extends CommandReplier<RangeReadings>
		implements RangeScanner {

	private final RotatingRangeScanner scanner;

	public PhysicalRangeScanner(NXTCommunicator communicator,
			RegulatedMotor head, RangeFinder rangeFinder) {
		super(communicator);
		communicator.addListener(this);

		scanner = new RotatingRangeScanner(head, rangeFinder);
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
		if (!(command instanceof ScanCommand))
			return;

		ScanCommand scanCommand = (ScanCommand) command;
		// Scan at given angles
		float[] angles = scanCommand.getAngles();
		setAngles(angles);
		RangeReadings readings = getRangeValues();
		// Reply with readings
		reply(scanCommand, readings);
	}

	@Override
	protected MessageType<Report<?>> getResponseType(
			MessageType<Command> requestType) {
		return ReportType.SCAN;
	}

}
