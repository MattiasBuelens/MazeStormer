package mazestormer.remote;

import java.io.IOException;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import lejos.robotics.RegulatedMotor;
import lejos.util.Delay;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.ScanCommand;
import mazestormer.report.RangeReadingReport;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class PhysicalRangeScanner extends lejos.robotics.RotatingRangeScanner {

	private final NXTCommunicator communicator;
	private final ScanReplier scanReplier;

	protected float gearRatio;

	public PhysicalRangeScanner(NXTCommunicator communicator, RegulatedMotor head, RangeFinder rangeFinder,
			float gearRatio) {
		super(head, rangeFinder);
		this.gearRatio = gearRatio;
		this.communicator = communicator;

		// Reply to scan commands
		scanReplier = new ScanReplier(communicator);
		communicator.addListener(scanReplier);
	}

	/**
	 * Set the gear ratio.
	 * 
	 * @param gearRatio
	 *            the gear ratio
	 */
	public void setGearRatio(float gearRatio) {
		this.gearRatio = gearRatio;
	}

	@Override
	public RangeReadings getRangeValues() {
		if (readings == null || readings.getNumReadings() != angles.length) {
			readings = new RangeReadings(angles.length);
		}

		for (int i = 0; i < angles.length; i++) {
			// Rotate and scan
			final float angle = angles[i];
			head.rotateTo((int) (angle * gearRatio));
			Delay.msDelay(50);
			float range = rangeFinder.getRange() + ZERO;
			if (range > MAX_RELIABLE_RANGE_READING) {
				range = -1;
			}
			// Make reading and trigger listeners
			final RangeReading reading = new RangeReading(angle, range);
			readings.set(i, reading);
			// Publish reading
			publishReading(reading);
		}
		// Reset head
		head.rotateTo(0);

		return readings;
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
		protected MessageType<Report<?>> getResponseType(MessageType<Command> requestType) {
			return ReportType.SCAN;
		}
	}

	/**
	 * Report range readings.
	 */
	public void publishReading(RangeReading reading) {
		try {
			communicator.send(new RangeReadingReport(ReportType.RANGE_READING, reading));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

}
