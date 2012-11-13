package mazestormer.remote;

import java.io.IOException;

import lejos.pc.comm.NXTConnector;
import mazestormer.command.Command;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class RemoteCommunicator extends Communicator<Command, Report> {

	private NXTConnector connector;

	public RemoteCommunicator(NXTConnector connector) {
		super(connector.getInputStream(), connector.getOutputStream());
		this.connector = connector;
	}

	@Override
	public MessageType<? extends Report> getType(int typeId) {
		return ReportType.values()[typeId];
	}

	@Override
	public void terminate() throws IOException {
		super.terminate();
		if (connector != null) {
			connector.close();
			connector = null;
		}
	}
}
