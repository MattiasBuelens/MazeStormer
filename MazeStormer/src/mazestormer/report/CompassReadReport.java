package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CompassReadReport extends RequestReport<Float> {

	private float degrees;

	public CompassReadReport(ReportType type) {
		super(type);
	}

	public CompassReadReport(ReportType type, int requestId, float degrees) {
		this(type);
		setRequestId(requestId);
		setDegrees(degrees);
	}

	public float getDegrees() {
		return degrees;
	}

	public void setDegrees(float degrees) {
		this.degrees = degrees;
	}

	@Override
	public Float getValue() {
		return getDegrees();
	}

	@Override
	public void setValue(Float value) {
		setDegrees(value);
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setDegrees(dis.readFloat());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeFloat(getDegrees());
	}

}
