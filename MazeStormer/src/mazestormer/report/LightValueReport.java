package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LightValueReport extends Report {

	private int lightValue;

	public LightValueReport(ReportType type) {
		super(type);
	}

	public LightValueReport(ReportType type, int lightValue) {
		this(type);
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
