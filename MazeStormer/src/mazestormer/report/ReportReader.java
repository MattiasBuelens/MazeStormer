package mazestormer.report;

import mazestormer.remote.MessageTypeReader;

public class ReportReader extends MessageTypeReader<Report<?>> {

	@Override
	public ReportType getType(int typeId) {
		return ReportType.values()[typeId];
	}

}
