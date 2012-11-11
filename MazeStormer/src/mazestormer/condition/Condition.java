package mazestormer.condition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.Transmittable;

public abstract class Condition implements Transmittable {

	private ConditionType type;
	private double parameter;

	protected Condition(ConditionType type) {
		this.type = type;
	}

	public final ConditionType getType() {
		return type;
	}

	private void setType(ConditionType type) {
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
		dos.writeInt(getType().ordinal());
		dos.writeDouble(getParameter());
	}

	@Override
	public void loadObject(DataInputStream dis) throws IOException {
		setType(ConditionType.values()[dis.readInt()]);
		setParameter(dis.readDouble());
	}

}
