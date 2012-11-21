package mazestormer.remote;

import java.io.IOException;

import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.robot.Robot;
import mazestormer.simulator.AbstractCalibratedLightSensor;
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
			return lightValueRequester.request().get(1000);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public void setFloodlight(boolean floodlight) {
		send(new LightFloodlightCommand(CommandType.LIGHT_FLOODLIGHT,
				floodlight));
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

	public static class LightValueRequester extends ReportRequester<Integer> {

		public LightValueRequester(RemoteCommunicator communicator) {
			super(communicator);
		}

		public Future<Integer> request() {
			return request(CommandType.LIGHT_READ);
		}

	}

	@Override
	public float getSensorRadius() {
		return Robot.sensorRadius;
	}

}
