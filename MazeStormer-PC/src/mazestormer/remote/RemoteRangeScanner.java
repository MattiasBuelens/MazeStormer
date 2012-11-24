package mazestormer.remote;

import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import mazestormer.command.CommandType;
import mazestormer.command.ScanCommand;
import mazestormer.util.Future;

public class RemoteRangeScanner extends RemoteComponent implements RangeScanner {

	private float[] angles;
	private final ScanRequester scanRequester;

	public RemoteRangeScanner(RemoteCommunicator communicator) {
		super(communicator);
		scanRequester = new ScanRequester(getCommunicator());
		setup();
	}

	private void setup() {
		addMessageListener(scanRequester);
	}

	protected float[] getAngles() {
		return angles;
	}

	@Override
	public void setAngles(float[] angles) {
		this.angles = angles;
	}

	@Override
	public RangeReadings getRangeValues() {
		try {
			return scanRequester.request(getAngles()).get(RemoteRobot.requestTimeout);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public RangeFinder getRangeFinder() {
		return null;
	}

	public static class ScanRequester extends ReportRequester<RangeReadings> {

		public ScanRequester(RemoteCommunicator communicator) {
			super(communicator);
		}

		public Future<RangeReadings> request(float[] angles) {
			ScanCommand command = new ScanCommand(CommandType.SCAN, angles);
			return request(command);
		}

	}

}
