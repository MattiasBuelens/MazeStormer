package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.util.CommandUtils;

public class IRReadReport extends RequestReport<float[]> {

	private float[] values;

	public IRReadReport(ReportType type) {
		this(type, new float[6]);
	}

	public IRReadReport(ReportType type, float[] values) {
		super(type);
		setValue(values);
	}

	public float getAngle() {
		return this.values[0];
	}

	public int getSensorValue(int id) {
		return (int) this.values[id];
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
		setValue(CommandUtils.readArray(dis));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		CommandUtils.writeArray(dos, getValue());
	}
}
