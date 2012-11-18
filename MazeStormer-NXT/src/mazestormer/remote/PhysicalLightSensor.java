package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.CommandType;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.command.LightReadCommand;
import mazestormer.report.ReportType;
import mazestormer.robot.CalibratedLightSensor;

public class PhysicalLightSensor extends LightSensor implements
		CalibratedLightSensor {

	private final NXTCommunicator communicator;
	private List<MessageListener<Command>> messageListeners = new ArrayList<MessageListener<Command>>();

	public PhysicalLightSensor(NXTCommunicator communicator) {
		super(SensorPort.S1);

		this.communicator = communicator;
		setup();
	}

	private void setup() {
		addMessageListener(new LightFloodlightCommandListener());
		addMessageListener(new LightValueReplier());
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		messageListeners.add(listener);
		communicator.addListener(listener);
	}

	public void terminate() {
		// Turn off flood light
		setFloodlight(false);

		// Remove registered message listeners
		for (MessageListener<Command> listener : messageListeners) {
			communicator.removeListener(listener);
		}
	}

	private class LightFloodlightCommandListener implements
			MessageListener<Command> {
		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof LightFloodlightCommand))
				return;

			boolean isFloodlight = ((LightFloodlightCommand) command)
					.isFloodlight();
			setFloodlight(isFloodlight);
		}
	}

	private class LightValueReplier extends CommandReplier<Integer> {

		public LightValueReplier() {
			super(communicator);
		}

		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof LightReadCommand))
				return;

			reply((LightReadCommand) command, getNormalizedLightValue());
		}

		@Override
		protected ReportType getResponseType(MessageType<Command> requestType) {
			if (requestType == CommandType.LIGHT_READ) {
				return ReportType.LIGHT_VALUE;
			}
			return null;
		}

	}

}
