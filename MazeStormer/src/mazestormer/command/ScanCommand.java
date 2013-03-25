package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.RangeReadings;
import mazestormer.util.CommandUtils;

public class ScanCommand extends RequestCommand<RangeReadings> {

	private float[] angles;

	public ScanCommand(CommandType type) {
		super(type);
	}

	public ScanCommand(CommandType type, float[] angles) {
		this(type);
		setAngles(angles);
	}

	public float[] getAngles() {
		return angles;
	}

	public void setAngles(float[] angles) {
		this.angles = angles;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setAngles(CommandUtils.readArray(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		CommandUtils.writeArray(dos, getAngles());
	}

}
