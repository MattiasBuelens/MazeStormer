package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.Message;

public abstract class Report implements Message {

	private final ReportType type;

	public Report(ReportType type) {
		this.type = type;
	}

	public ReportType getType() {
		return type;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		// setType(ReportType.values()[dis.readInt()]);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(getType().ordinal());
	}

}
