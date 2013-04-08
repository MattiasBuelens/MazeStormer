package mazestormer.remote;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRSeekerV2;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.CommandType;
import mazestormer.command.IRReadCommand;
import mazestormer.report.ReportType;
import mazestormer.robot.IRSensor;

public class PhysicalIRSensor extends IRSeekerV2 implements IRSensor {

	private final NXTCommunicator communicator;

	public PhysicalIRSensor(NXTCommunicator communicator, SensorPort port) {
		super(port, Mode.AC);
		this.communicator = communicator;
		setup();
	}

	private void setup() {
		// Add message listeners
		addMessageListener(new IRValueReplier());
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		// messageListeners.add(listener);
		communicator.addListener(listener);
	}

	@Override
	public boolean hasReading() {
		return !Float.isNaN(getAngle());
	}

	/**
	 * Handles ir value requests.
	 */
	private class IRValueReplier extends CommandReplier<float[]> {

		public IRValueReplier() {
			super(communicator);
		}

		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof IRReadCommand))
				return;
			IRReadCommand comm = (IRReadCommand) command;

			float[] values = new float[6];
			values[0] = getAngle();
			for (int i = 1; i <= 5; i++) {
				values[i] = getSensorValue(i);
			}
			reply(comm, values);
		}

		@Override
		protected ReportType getResponseType(MessageType<Command> requestType) {
			if (requestType == CommandType.IR_READ) {
				return ReportType.IR_VALUE;
			}
			return null;
		}

	}

}
