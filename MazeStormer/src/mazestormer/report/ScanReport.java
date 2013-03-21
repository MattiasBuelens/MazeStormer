package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.RangeReadings;

public class ScanReport extends RequestReport<RangeReadings> {

	private RangeReadings readings;

	public ScanReport(ReportType type) {
		super(type);
	}

	public RangeReadings getReadings() {
		return readings;
	}

	public void setReadings(RangeReadings readings) {
		this.readings = readings;
	}

	@Override
	public RangeReadings getValue() {
		return getReadings();
	}

	@Override
	public void setValue(RangeReadings value) {
		setReadings(value);
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);

		RangeReadings readings = new RangeReadings(0);
		readings.loadObject(dis);
		setReadings(readings);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		getReadings().dumpObject(dos);
	}

}
