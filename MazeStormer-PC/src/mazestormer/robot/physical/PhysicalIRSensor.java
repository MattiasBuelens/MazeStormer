package mazestormer.robot.physical;

import java.util.concurrent.TimeUnit;

import mazestormer.command.CommandType;
import mazestormer.command.IRReadCommand;
import mazestormer.robot.IRSensor;
import mazestormer.util.Future;

public class PhysicalIRSensor extends PhysicalComponent implements IRSensor {

	private final IRRequester irRequester;

	public PhysicalIRSensor(PhysicalCommunicator communicator) {
		super(communicator);
		irRequester = new IRRequester(getCommunicator());
		setup();
	}

	private void setup() {
		addMessageListener(irRequester);
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}

	@Override
	public float getAngle() {
		return getValues()[0];
	}

	public int getSensorValue(int id) {
		return (int) getValues()[id];
	}

	private float[] getValues() {
		try {
			return irRequester.request().get(PhysicalRobot.requestTimeout,
					TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class IRRequester extends ReportRequester<float[]> {

		public IRRequester(PhysicalCommunicator communicator) {
			super(communicator);
		}

		public Future<float[]> request() {
			IRReadCommand command = new IRReadCommand(CommandType.IR_READ);
			return request(command);
		}

	}

}
