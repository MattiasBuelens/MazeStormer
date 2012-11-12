package mazestormer.remote;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import lejos.pc.comm.NXTConnector;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class RemoteCommunicator extends Communicator {

	private final NXTConnector connector;
	private final Factories factories;

	public RemoteCommunicator(NXTConnector connector, Factories factories) {
		super(connector.getInputStream(), connector.getOutputStream());
		this.connector = connector;
		this.factories = factories;
	}

	public RemoteCommunicator(NXTConnector connector) {
		this(connector, Factories.getInstance());
	}

	@Override
	public Report receive() throws IllegalStateException, IOException {
		// Read type
		int typeId = dis().readInt();
		ReportType type = ReportType.values()[typeId];
		checkState(type != null, "Unknown report type identifier: " + typeId);

		// Read report
		Report report = factories.get(type).create();
		report.loadObject(dis());
		return report;
	}

}
