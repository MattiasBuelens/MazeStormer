package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.Transmittable;

public abstract class Command implements Transmittable {

	private long id;
	private CommandType type;
	private double parameter;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CommandType getType() {
		return type;
	}

	public void setType(CommandType type) {
		this.type = type;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

	@Override
	public void dumpObject(DataOutputStream dos) throws IOException {
		//dos.writeInt(getType().ordinal());
		dos.writeLong(getId());
		dos.writeDouble(getParameter());
	}

	@Override
	public void loadObject(DataInputStream dis) throws IOException {
		//setType(CommandType.values()[dis.readInt()]);
		setId(dis.readLong());
		setParameter(dis.readDouble());
	}

}
