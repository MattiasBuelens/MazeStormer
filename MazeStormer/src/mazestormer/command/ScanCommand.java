package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.RangeReadings;

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
		setAngles(readArray(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		writeArray(dos, getAngles());
	}

	public static float[] readArray(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = dis.readFloat();
		}
		return array;
	}

	public static void writeArray(DataOutputStream dos, float[] array)
			throws IOException {
		dos.writeInt(array.length);
		for (float value : array) {
			dos.writeFloat(value);
		}
	}

}
