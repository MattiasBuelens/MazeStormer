package mazestormer.remote;

import lejos.robotics.LampLightDetector;
import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.report.Report;

public class RemoteLightSensor extends RemoteComponent implements
		LampLightDetector {

	private boolean isFloodlight = false;

	public RemoteLightSensor(Communicator<Command, Report> communicator) {
		super(communicator);
		setup();
	}

	private void setup() {

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
		// TODO Auto-generated method stub
		return 0;
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

}
