package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.RangeReading;

public class RangeReadingReport extends Report<RangeReading> {

	private RangeReading reading;

	public RangeReadingReport(ReportType type) {
		this(type, null);
	}

	public RangeReadingReport(ReportType type, RangeReading reading) {
		super(type);
		setReading(reading);
	}

	public RangeReading getReading() {
		return reading;
	}

	public void setReading(RangeReading reading) {
		this.reading = reading;
	}

	@Override
	public RangeReading getValue() {
		return getReading();
	}

	@Override
	public void setValue(RangeReading value) {
		setReading(value);
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		float angle = dis.readFloat();
		float range = dis.readFloat();
		setReading(new RangeReading(angle, range));
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeFloat(getReading().getAngle());
		dos.writeFloat(getReading().getRange());
	}

}
