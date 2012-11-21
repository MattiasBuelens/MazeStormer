package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightCalibrateCommand extends Command {

	private int value;

	public LightCalibrateCommand(CommandType type) {
		super(type);
	}

	public LightCalibrateCommand(CommandType type, int value) {
		this(type);
		setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setValue(dis.readInt());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(getValue());
	}

}
