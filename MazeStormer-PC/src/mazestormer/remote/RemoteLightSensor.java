package mazestormer.remote;

import lejos.robotics.LampLightDetector;
import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.command.LightFloodlightCommand;
import mazestormer.command.LightReadCommand;
import mazestormer.report.LightReadReport;
import mazestormer.report.Report;
import mazestormer.report.RequestFuture;

public class RemoteLightSensor extends RemoteComponent implements
		LampLightDetector {

	private boolean isFloodlight = false;

	public RemoteLightSensor(Communicator<Command, Report> communicator) {
		super(communicator);
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
		// Create command
		LightReadCommand command = new LightReadCommand(CommandType.LIGHT_READ);
		command.setRequestId(getCommunicator().nextRequestId());
		// Create future listener
		LightReadFuture future = new LightReadFuture(command);
		// Send command
		send(command);
		// Wait for response
		return future.get();
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

	private class LightReadFuture extends RequestFuture<Integer> {

		public LightReadFuture(LightReadCommand request) {
			super(request, getCommunicator());
		}

		@Override
		protected boolean isResponse(RequestMessage message) {
			return (message instanceof LightReadReport);
		}

		@Override
		protected Integer getResponse(RequestMessage message) {
			LightReadReport report = (LightReadReport) message;
			return report.getLightValue();
		}

	}

}
