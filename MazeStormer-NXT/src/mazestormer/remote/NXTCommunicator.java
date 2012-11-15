package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.comm.NXTConnection;
import mazestormer.command.Command;
import mazestormer.command.CommandReader;
import mazestormer.report.Report;

public class NXTCommunicator extends Communicator<Report, Command> {

	private NXTConnection connection;

	public NXTCommunicator(NXTConnection connection) {
		super(connection.openInputStream(), connection.openOutputStream(),
				new CommandReader());
		this.connection = connection;
	}

	@Override
	public void terminate() throws IOException {
		super.terminate();
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

}
