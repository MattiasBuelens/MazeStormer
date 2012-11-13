package mazestormer.condition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.Message;

public abstract class Condition implements Message {

	private final ConditionType type;
	private double parameter;

	public Condition(ConditionType type) {
		this.type = type;
	}

	public ConditionType getType() {
		return type;
	}

	public double getParameter() {
		return parameter;
	}

	public void setParameter(double parameter) {
		this.parameter = parameter;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		// setType(ConditionType.values()[dis.readInt()]);
		setParameter(dis.readDouble());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		// dos.writeInt(getType().ordinal());
		dos.writeDouble(getParameter());
	}

}
