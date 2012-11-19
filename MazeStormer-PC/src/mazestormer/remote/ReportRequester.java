package mazestormer.remote;

import java.util.HashMap;
import java.util.Map;

import mazestormer.command.Command;
import mazestormer.command.RequestCommand;
import mazestormer.report.Report;
import mazestormer.report.RequestReport;
import mazestormer.util.Future;

public class ReportRequester<V> extends MessageSender<RequestCommand<V>>
		implements MessageListener<Report<?>> {

	private Map<Integer, RequestFuture<V>> futures = new HashMap<Integer, RequestFuture<V>>();

	public ReportRequester(RemoteCommunicator communicator) {
		super(communicator);
	}

	@Override
	public RemoteCommunicator getCommunicator() {
		return (RemoteCommunicator) super.getCommunicator();
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
		RequestFuture<V> future = new RequestFuture<V>(request);
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
		RequestFuture<V> future = futures.get(requestId);

		// Remove future if report resolves it
		if (future != null && future.tryResolve(requestReport)) {
			futures.remove(requestId);
		}
	}

}
