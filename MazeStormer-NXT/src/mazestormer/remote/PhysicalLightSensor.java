package mazestormer.remote;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.CommandType;
import mazestormer.command.ConditionalCommandListener;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.command.LightReadCommand;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.condition.LightCompareCondition;
import mazestormer.report.ReportType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.Robot;

public class PhysicalLightSensor extends LightSensor implements
		CalibratedLightSensor, SensorPortListener {

	private final SensorPort port;
	private List<LightValueListener> lightListeners = new ArrayList<LightValueListener>();

	private final NXTCommunicator communicator;
	private List<MessageListener<Command>> messageListeners = new ArrayList<MessageListener<Command>>();

	public PhysicalLightSensor(NXTCommunicator communicator, SensorPort port) {
		super(port);
		this.port = port;
		this.communicator = communicator;
		setup();
	}

	public PhysicalLightSensor(NXTCommunicator communicator) {
		this(communicator, SensorPort.S1);
	}

	private void setup() {
		// Add as port listener
		SensorPortListeners.get(port).addSensorPortListener(this);

		// Add message listeners
		addMessageListener(new LightFloodlightCommandListener());
		addMessageListener(new LightValueReplier());
		addMessageListener(new LightConditionListener());
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		messageListeners.add(listener);
		communicator.addListener(listener);
	}

	public void addLightListener(LightValueListener listener) {
		lightListeners.add(listener);
	}

	public void removeLightListener(LightValueListener listener) {
		lightListeners.remove(listener);
	}

	protected void callLightListeners(int normalizedLightValue) {
		for (LightValueListener listener : lightListeners) {
			listener.lightValueChanged(normalizedLightValue);
		}
	}

	@Override
	public void stateChanged(SensorPort source, int oldValue, int newValue) {
		if (source == port) {
			int normalizedLightValue = 1023 - newValue;
			callLightListeners(normalizedLightValue);
		}
	}

	public void terminate() {
		// Turn off flood light
		setFloodlight(false);

		// Remove as port listener
		SensorPortListeners.get(port).removeSensorPortListener(this);

		// Remove registered message listeners
		for (MessageListener<Command> listener : messageListeners) {
			communicator.removeListener(listener);
		}
	}

	/**
	 * Handles flood light commands.
	 */
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

	/**
	 * Handles light value requests.
	 */
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

	/**
	 * Handles light value conditions.
	 */
	private class LightConditionListener extends ConditionalCommandListener {

		public LightConditionListener() {
			super(communicator);
		}

		@Override
		public ConditionFuture createFuture(Condition condition) {
			switch (condition.getType()) {
			case LIGHT_GREATER_THAN:
			case LIGHT_SMALLER_THAN:
				return new LightConditionFuture(
						(LightCompareCondition) condition);
			default:
				return null;
			}
		}

	}

	/**
	 * Resolves a light value condition.
	 */
	private class LightConditionFuture extends ConditionFuture implements
			LightValueListener {

		public LightConditionFuture(LightCompareCondition condition) {
			super(condition);
			addLightListener(this);
		}

		@Override
		public LightCompareCondition getCondition() {
			return (LightCompareCondition) super.getCondition();
		}

		@Override
		public void lightValueChanged(int normalizedLightValue) {
			if (matches(normalizedLightValue)) {
				removeLightListener(this);
				resolve();
			}
		}

		public boolean matches(int normalizedLightValue) {
			int threshold = getCondition().getThreshold();
			switch (getCondition().getType()) {
			case LIGHT_GREATER_THAN:
				return normalizedLightValue >= threshold;
			case LIGHT_SMALLER_THAN:
				return normalizedLightValue <= threshold;
			default:
				return false;
			}
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			removeLightListener(this);
			return super.cancel(mayInterruptIfRunning);
		}

	}

	@Override
	public int getNormalizedLightValue(int lightValue) {
		if (getHigh() == getLow())
			return getLow();
		return (int) ((lightValue / 100f) * (getHigh() - getLow()) + getLow());
	}

	@Override
	public int getLightValue(int normalizedLightValue) {
		if (getHigh() == getLow())
			return 0;
		return 100 * (normalizedLightValue - getLow()) / (getHigh() - getLow());
	}

	@Override
	public float getSensorRadius() {
		return Robot.sensorRadius;
	}

}
