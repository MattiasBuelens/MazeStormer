package mazestormer.condition;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.Message;

public abstract class Condition implements Message {

	private final ConditionType type;

	public Condition(ConditionType type) {
		this.type = type;
	}

	public ConditionType getType() {
		return type;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		// setType(ConditionType.values()[dis.readInt()]);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getType().ordinal());
	}

}
