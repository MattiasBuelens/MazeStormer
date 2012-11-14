package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightFloodlightCommand extends Command {

	private boolean isFloodlight;

	public LightFloodlightCommand(CommandType type) {
		super(type);
	}

	public LightFloodlightCommand(CommandType type, boolean isFloodlight) {
		this(type);
		setFloodlight(isFloodlight);
	}

	public boolean isFloodlight() {
		return isFloodlight;
	}

	public void setFloodlight(boolean isFloodlight) {
		this.isFloodlight = isFloodlight;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setFloodlight(dis.readBoolean());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeBoolean(isFloodlight());
	}

}
