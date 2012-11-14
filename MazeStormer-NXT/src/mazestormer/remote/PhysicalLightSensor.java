package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import mazestormer.command.Command;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.command.LightReadCommand;
import mazestormer.report.LightReadReport;
import mazestormer.report.Report;
import mazestormer.report.ReportType;
import mazestormer.robot.CalibratedLightSensor;

public class PhysicalLightSensor extends LightSensor implements
		CalibratedLightSensor {

	private final Communicator<Report, Command> communicator;
	private List<MessageListener<Command>> messageListeners = new ArrayList<MessageListener<Command>>();

	public PhysicalLightSensor(Communicator<Report, Command> communicator) {
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

	private class LightValueReplier extends MessageReplier<Report, Command> {

		public LightValueReplier() {
			super(communicator);
		}

		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof LightReadCommand))
				return;

			int requestId = ((LightReadCommand) command).getRequestId();
			int lightValue = getNormalizedLightValue();
			report(new LightReadReport(ReportType.LIGHT_VALUE, requestId,
					lightValue));
		}

	}

}
