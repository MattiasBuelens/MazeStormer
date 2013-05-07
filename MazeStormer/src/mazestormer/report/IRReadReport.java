package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IRReadReport extends RequestReport<float[]> {

	private float[] values;

	public IRReadReport(ReportType type) {
		this(type, new float[6]);
	}

	public IRReadReport(ReportType type, float[] values) {
		super(type);
		setValue(values);
	}

	@Override
	public float[] getValue() {
		return values;
	}

	@Override
	public void setValue(float[] values) {
		this.values = values;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setValue(readArray(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		writeArray(dos, getValue());
	}

	public static float[] readArray(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = dis.readFloat();
		}
		return array;
	}

	public static void writeArray(DataOutputStream dos, float[] array) throws IOException {
		dos.writeInt(array.length);
		for (float value : array) {
			dos.writeFloat(value);
		}
	}

}
