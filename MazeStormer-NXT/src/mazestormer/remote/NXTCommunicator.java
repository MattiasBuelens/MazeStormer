package mazestormer.remote;

import java.io.IOException;

import lejos.nxt.comm.NXTConnection;
import mazestormer.command.Command;
import mazestormer.command.CommandType;

public class NXTCommunicator extends Communicator {

	private final NXTConnection connection;
	private final Factories factories;

	public NXTCommunicator(NXTConnection connection, Factories factories) {
		super(connection.openInputStream(), connection.openOutputStream());
		this.connection = connection;
		this.factories = factories;
	}

	public NXTCommunicator(NXTConnection connection) {
		this(connection, Factories.getInstance());
	}

	@Override
	public Command receive() throws IllegalStateException, IOException {
		// Read type
		int typeId = dis().readInt();
		CommandType type = CommandType.values()[typeId];
		if (type == null) {
			throw new IllegalStateException("Unknown report type identifier: "
					+ typeId);
		}

		// Read command
		Command command = factories.get(type).create();
		command.loadObject(dis());
		return command;
	}

}
