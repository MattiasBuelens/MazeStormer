package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.Message;

public abstract class Command implements Message {

	private final CommandType type;

	public Command(CommandType type) {
		this.type = type;
	}

	public CommandType getType() {
		return type;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		// setType(CommandType.values()[dis.readInt()]);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getType().ordinal());
	}

}
