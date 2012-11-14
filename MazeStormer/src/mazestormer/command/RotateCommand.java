package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RotateCommand extends Command {

	private double angle;

	public RotateCommand(CommandType type) {
		super(type);
	}

	public RotateCommand(CommandType type, double angle) {
		this(type);
		setAngle(angle);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setAngle(dis.readDouble());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeDouble(getAngle());
	}
}
