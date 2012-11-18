package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.remote.MessageType;
import mazestormer.remote.RequestMessage;
import mazestormer.report.Report;
import mazestormer.report.RequestReport;

public abstract class RequestCommand<V> extends Command implements
		RequestMessage {

	private int requestId;

	public RequestCommand(CommandType type) {
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

	public RequestReport<V> createResponse(MessageType<Report<?>> reportType,
			V value) {
		@SuppressWarnings("unchecked")
		RequestReport<V> report = (RequestReport<V>) reportType.build();
		report.setRequestId(getRequestId());
		report.setValue(value);
		return report;
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
