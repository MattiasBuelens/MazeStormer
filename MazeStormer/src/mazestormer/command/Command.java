package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.Message;

public abstract class Command implements Message {

	private final CommandType type;
	private long id;
	private double parameter;

	public Command(CommandType type) {
		this.type = type;
	}

	public CommandType getType() {
		return type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		// setType(CommandType.values()[dis.readInt()]);
		setId(dis.readLong());
		setParameter(dis.readDouble());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		// dos.writeInt(getType().ordinal());
		dos.writeLong(getId());
		dos.writeDouble(getParameter());
	}

}
