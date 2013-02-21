package mazestormer.physical;

import java.util.HashMap;
import java.util.Map;

import mazestormer.command.Command;
import mazestormer.command.RequestCommand;
import mazestormer.remote.MessageListener;
import mazestormer.remote.MessageSender;
import mazestormer.remote.MessageType;
import mazestormer.report.Report;
import mazestormer.report.RequestReport;
import mazestormer.util.Future;

public class PhysicalRequester<V> extends MessageSender<RequestCommand<V>>
		implements MessageListener<Report<?>> {

	private Map<Integer, PhysicalFuture<V>> futures = new HashMap<Integer, PhysicalFuture<V>>();

	public PhysicalRequester(PhysicalCommunicator communicator) {
		super(communicator);
	}

	@Override
	public PhysicalCommunicator getCommunicator() {
		return (PhysicalCommunicator) super.getCommunicator();
	}

	protected Future<V> request(MessageType<Command> requestType) {
		// Create request
		@SuppressWarnings("unchecked")
		RequestCommand<V> request = (RequestCommand<V>) requestType.build();

		return request(request);
	}

	protected Future<V> request(RequestCommand<V> request) {
		// Set request identifier
		int requestId = getCommunicator().nextRequestId();
		request.setRequestId(requestId);

		// Create future
		PhysicalFuture<V> future = new PhysicalFuture<V>(request);
		futures.put(requestId, future);

		// Send request
		send(request);
		return future;
	}

	@Override
	public void messageReceived(Report<?> report) {
		if (!(report instanceof RequestReport<?>))
			return;

		// Get request report and associated future
		@SuppressWarnings("unchecked")
		RequestReport<V> requestReport = (RequestReport<V>) report;
		int requestId = requestReport.getRequestId();
		PhysicalFuture<V> future = futures.get(requestId);

		// Remove future if report resolves it
		if (future != null && future.tryResolve(requestReport)) {
			futures.remove(requestId);
		}
	}

}
