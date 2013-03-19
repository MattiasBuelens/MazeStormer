package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TravelCommand extends Command {

	private double distance;

	public TravelCommand(CommandType type) {
		super(type);
	}

	public TravelCommand(CommandType type, double distance) {
		this(type);
		setDistance(distance);
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setDistance(dis.readDouble());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeDouble(getDistance());
	}

}
