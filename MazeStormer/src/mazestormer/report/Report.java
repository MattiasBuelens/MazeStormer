package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.robotics.Transmittable;

public abstract class Report implements Transmittable {

	private ReportType type;

	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	@Override
	public void dumpObject(DataOutputStream dos) throws IOException {
		dos.writeInt(getType().ordinal());
	}

	@Override
	public void loadObject(DataInputStream dis) throws IOException {
		setType(ReportType.values()[dis.readInt()]);
	}

}
