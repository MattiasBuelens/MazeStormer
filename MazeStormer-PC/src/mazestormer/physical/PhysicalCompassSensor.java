package mazestormer.physical;

import java.util.concurrent.TimeUnit;

import mazestormer.command.CommandType;
import mazestormer.robot.CompassSensor;
import mazestormer.util.Future;

public class PhysicalCompassSensor extends PhysicalComponent implements
		CompassSensor {

	private final CompassValueRequester compassValueRequester;

	public PhysicalCompassSensor(PhysicalCommunicator communicator) {
		super(communicator);
		compassValueRequester = new CompassValueRequester(communicator);
		setup();
	}

	private void setup() {
		getCommunicator().addListener(compassValueRequester);
	}

	public void terminate() {
		getCommunicator().removeListener(compassValueRequester);
	}

	@Override
	public float getDegrees() {
		try {
			return compassValueRequester.request().get(
					PhysicalRobot.requestTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static class CompassValueRequester extends ReportRequester<Float> {

		public CompassValueRequester(PhysicalCommunicator communicator) {
			super(communicator);
		}

		public Future<Float> request() {
			return request(CommandType.COMPASS_READ);
		}

	}

}
