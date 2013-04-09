package mazestormer.physical;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReading;
import lejos.robotics.RangeReadings;
import mazestormer.command.CommandType;
import mazestormer.command.ScanCommand;
import mazestormer.remote.MessageListener;
import mazestormer.report.RangeReadingReport;
import mazestormer.report.Report;
import mazestormer.robot.ObservableRangeScanner;
import mazestormer.robot.RangeScannerListener;
import mazestormer.util.Future;

public class PhysicalRangeScanner extends PhysicalComponent implements ObservableRangeScanner {

	private float[] angles;
	private final List<RangeScannerListener> listeners = new ArrayList<RangeScannerListener>();

	private final ScanRequester scanRequester;
	private final RangeReadingReceiver readingReceiver;

	public PhysicalRangeScanner(PhysicalCommunicator communicator) {
		super(communicator);

		scanRequester = new ScanRequester(getCommunicator());
		addMessageListener(scanRequester);

		readingReceiver = new RangeReadingReceiver();
		addMessageListener(readingReceiver);
	}

	protected float[] getAngles() {
		return angles;
	}

	@Override
	public void setAngles(float[] angles) {
		this.angles = angles.clone();
	}

	@Override
	public RangeReadings getRangeValues() {
		try {
			return scanRequester.request(getAngles()).get(PhysicalRobot.requestTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public RangeFinder getRangeFinder() {
		return null;
	}

	@Override
	public void addListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(RangeScannerListener listener) {
		listeners.add(listener);
	}

	private void fireReadingReceived(RangeReading reading) {
		for (RangeScannerListener listener : listeners) {
			listener.readingReceived(reading);
		}
	}

	public static class ScanRequester extends ReportRequester<RangeReadings> {

		public ScanRequester(PhysicalCommunicator communicator) {
			super(communicator);
		}

		public Future<RangeReadings> request(float[] angles) {
			ScanCommand command = new ScanCommand(CommandType.SCAN, angles);
			return request(command);
		}

	}

	private class RangeReadingReceiver implements MessageListener<Report<?>> {

		@Override
		public void messageReceived(Report<?> report) {
			if (report instanceof RangeReadingReport) {
				fireReadingReceived(((RangeReadingReport) report).getReading());
			}
		}

	}

}
