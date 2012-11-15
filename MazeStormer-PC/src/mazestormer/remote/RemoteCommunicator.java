package mazestormer.remote;

import java.io.IOException;

import lejos.pc.comm.NXTConnector;
import mazestormer.command.Command;
import mazestormer.report.Report;
import mazestormer.report.ReportReader;

public class RemoteCommunicator extends Communicator<Command, Report> {

	private NXTConnector connector;

	public RemoteCommunicator(NXTConnector connector) {
		super(connector.getInputStream(), connector.getOutputStream(),
				new ReportReader());
		this.connector = connector;
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
