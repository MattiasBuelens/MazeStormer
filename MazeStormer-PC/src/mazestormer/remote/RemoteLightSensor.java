package mazestormer.remote;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.command.LightCalibrateCommand;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.robot.AbstractCalibratedLightSensor;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

public class RemoteLightSensor extends AbstractCalibratedLightSensor {

	private boolean isFloodlight = false;
	private final LightValueRequester lightValueRequester;

	private final RemoteCommunicator communicator;

	public RemoteLightSensor(RemoteCommunicator communicator) {
		this.communicator = communicator;
		lightValueRequester = new LightValueRequester(communicator);
		setup();
	}

	private void setup() {
		communicator.addListener(lightValueRequester);
	}

	public void terminate() {
		communicator.removeListener(lightValueRequester);
	}

	private void send(Command command) {
		try {
			communicator.send(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getNormalizedLightValue() {
		try {
			return lightValueRequester.request().get(RemoteRobot.requestTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void setLow(int low) {
		super.setLow(low);
		send(new LightCalibrateCommand(CommandType.LIGHT_SET_LOW, low));
	}

	@Override
	public void setHigh(int high) {
		super.setHigh(high);
		send(new LightCalibrateCommand(CommandType.LIGHT_SET_HIGH, high));
	}

	@Override
	public void setFloodlight(boolean floodlight) {
		send(new LightFloodlightCommand(CommandType.LIGHT_FLOODLIGHT, floodlight));
	}

	@Override
	public boolean isFloodlightOn() {
		return isFloodlight;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getFloodlight() {
		return 0;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public boolean setFloodlight(int color) {
		return false;
	}

	@Override
	public float getSensorRadius() {
		return ControllableRobot.sensorRadius;
	}

	public static class LightValueRequester extends ReportRequester<Integer> {

		public LightValueRequester(RemoteCommunicator communicator) {
			super(communicator);
		}

		public Future<Integer> request() {
			return request(CommandType.LIGHT_READ);
		}

	}

}
