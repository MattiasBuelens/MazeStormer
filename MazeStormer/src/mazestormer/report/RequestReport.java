package mazestormer.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.RequestMessage;

public abstract class RequestReport<V> extends Report<V> implements
		RequestMessage {

	private int requestId;

	public RequestReport(ReportType type) {
		super(type);
	}

	@Override
	public int getRequestId() {
		return requestId;
	}

	@Override
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setRequestId(dis.readInt());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(getRequestId());
	}

}