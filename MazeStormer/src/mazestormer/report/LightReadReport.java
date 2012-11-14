package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightReadReport extends RequestReport {

	private int lightValue;

	public LightReadReport(ReportType type) {
		super(type);
	}

	public LightReadReport(ReportType type, int requestId, int lightValue) {
		this(type);
		setRequestId(requestId);
		setLightValue(lightValue);
	}

	public int getLightValue() {
		return lightValue;
	}

	public void setLightValue(int lightValue) {
		this.lightValue = lightValue;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setLightValue(dis.readInt());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(getLightValue());
	}

}
