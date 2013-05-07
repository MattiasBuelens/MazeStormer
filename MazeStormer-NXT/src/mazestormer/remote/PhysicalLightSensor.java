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
import mazestormer.command.LightCalibrateCommand;
import mazestormer.command.LightReadCommand;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.condition.LightCompareCondition;
import mazestormer.report.ReportType;
import mazestormer.robot.CalibratedLightSensor;
import mazestormer.robot.ControllableRobot;

public class PhysicalLightSensor extends LightSensor implements CalibratedLightSensor, SensorPortListener,
		MessageListener<Command> {

	private final SensorPort port;
	private List<LightConditionFuture> lightListeners = new ArrayList<LightConditionFuture>();

	private final NXTCommunicator communicator;

	// private List<MessageListener<Command>> messageListeners = new
	// ArrayList<MessageListener<Command>>();

	public PhysicalLightSensor(NXTCommunicator communicator, SensorPort port) {
		super(port);
		this.port = port;
		this.communicator = communicator;
		setup();
	}

	private void setup() {
		// Add as port listener
		SensorPortListeners.get(port).addSensorPortListener(this);

		// Add message listeners
		addMessageListener(this);
		addMessageListener(new LightValueReplier());
		addMessageListener(new LightConditionListener());
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
		return ControllableRobot.sensorRadius;
	}

	private void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		// messageListeners.add(listener);
		communicator.addListener(listener);
	}

	public void addLightListener(LightConditionFuture listener) {
		lightListeners.add(listener);
	}

	public void removeLightListener(LightConditionFuture listener) {
		lightListeners.remove(listener);
	}

	protected void callLightListeners(final int normalizedLightValue) {
		// Clone listeners array for safe iteration
		final LightConditionFuture[] listeners = lightListeners
				.toArray(new LightConditionFuture[lightListeners.size()]);
		// Call listeners
		for (LightConditionFuture listener : listeners) {
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

		// Remove registered light listeners
		for (LightConditionFuture listener : lightListeners) {
			removeLightListener(listener);
		}

		// Remove registered message listeners
		// for (MessageListener<Command> listener : messageListeners) {
		// communicator.removeListener(listener);
		// }
	}

	@Override
	public void messageReceived(Command command) {
		// if (command instanceof LightFloodlightCommand) {
		// onFloodLightCommand((LightFloodlightCommand) command);
		// }
		if (command instanceof LightCalibrateCommand) {
			onCalibrateCommand((LightCalibrateCommand) command);
		}
	}

	/**
	 * Handles flood light commands.
	 */
	// private void onFloodLightCommand(LightFloodlightCommand command) {
	// setFloodlight(command.isFloodlight());
	// }

	/**
	 * Handles calibration commands.
	 */
	private void onCalibrateCommand(LightCalibrateCommand command) {
		int value = command.getValue();
		switch (command.getType()) {
		case LIGHT_SET_LOW:
			setLow(value);
			break;
		case LIGHT_SET_HIGH:
			setHigh(value);
			break;
		default:
			break;
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
				return new LightConditionFuture((LightCompareCondition) condition);
			default:
				return null;
			}
		}

	}

	/**
	 * Resolves a light value condition.
	 */
	private class LightConditionFuture extends ConditionFuture {

		public LightConditionFuture(LightCompareCondition condition) {
			super(condition);
			addLightListener(this);
		}

		@Override
		public LightCompareCondition getCondition() {
			return (LightCompareCondition) super.getCondition();
		}

		public void lightValueChanged(int normalizedLightValue) {
			int lightValue = getLightValue(normalizedLightValue);
			if (matches(lightValue)) {
				// Remove as light listener
				removeLightListener(this);
				/*
				 * Resolve in separate thread to ensure no deadlocks can occur
				 * on the light sensor port listener.
				 */
				new Thread(new Runnable() {
					@Override
					public void run() {
						resolve();
					}
				}).start();
			}
		}

		public boolean matches(int lightValue) {
			int threshold = getCondition().getThreshold();
			switch (getCondition().getType()) {
			case LIGHT_GREATER_THAN:
				return lightValue >= threshold;
			case LIGHT_SMALLER_THAN:
				return lightValue <= threshold;
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

}
