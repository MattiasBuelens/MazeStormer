package mazestormer.physical;

import mazestormer.command.RequestCommand;
import mazestormer.report.RequestReport;
import mazestormer.util.AbstractFuture;

public class PhysicalFuture<V> extends AbstractFuture<V> {

	private final int requestId;

	public PhysicalFuture(RequestCommand<V> request) {
		this.requestId = request.getRequestId();
	}

	public boolean tryResolve(RequestReport<V> message) {
		if (message.getRequestId() == requestId) {
			return resolve(message.getValue());
		}
		return false;
	}

}
