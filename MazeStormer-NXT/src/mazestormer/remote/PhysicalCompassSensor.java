package mazestormer.remote;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.CompassHTSensor;
import mazestormer.command.Command;
import mazestormer.command.CommandReplier;
import mazestormer.command.CommandType;
import mazestormer.command.CompassReadCommand;
import mazestormer.report.ReportType;
import mazestormer.robot.CompassSensor;

public class PhysicalCompassSensor extends CompassHTSensor implements
		CompassSensor {

	public PhysicalCompassSensor(NXTCommunicator communicator) {
		super(SensorPort.S3);
		communicator.addListener(new CompassReplier(communicator));
	}

	/**
	 * Handles compass read requests.
	 */
	private class CompassReplier extends CommandReplier<Float> {

		public CompassReplier(NXTCommunicator communicator) {
			super(communicator);
		}

		@Override
		public void messageReceived(Command command) {
			if (!(command instanceof CompassReadCommand))
				return;

			reply((CompassReadCommand) command, getDegrees());
		}

		@Override
		protected ReportType getResponseType(MessageType<Command> requestType) {
			if (requestType == CommandType.COMPASS_READ) {
				return ReportType.COMPASS_VALUE;
			}
			return null;
		}

	}

}
