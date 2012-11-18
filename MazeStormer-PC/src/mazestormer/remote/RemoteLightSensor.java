package mazestormer.remote;

import lejos.robotics.LampLightDetector;
import mazestormer.command.CommandType;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.util.Future;

public class RemoteLightSensor extends RemoteComponent implements LampLightDetector {

	private boolean isFloodlight = false;
	private final LightValueRequester lightValueRequester;

	public RemoteLightSensor(RemoteCommunicator communicator) {
		super(communicator);
		lightValueRequester = new LightValueRequester(communicator);
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getLightValue() {
		return 0;
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

	/**
	 * Not implemented.
	 */
	@Override
	public int getHigh() {
		return 0;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public int getLow() {
		return 0;
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

	public static class LightValueRequester extends ReportRequester<Integer> {

		public LightValueRequester(RemoteCommunicator communicator) {
			super(communicator);
		}

		public Future<Integer> request() {
			return request(CommandType.LIGHT_READ);
		}

	}

}
