package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PilotParameterCommand extends Command {

	private double value;

	public PilotParameterCommand(CommandType type) {
		super(type);
	}

	public PilotParameterCommand(CommandType type, double value) {
		this(type);
		setValue(value);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setValue(dis.readDouble());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeDouble(getValue());
	}

}
