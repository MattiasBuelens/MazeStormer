package mazestormer.report;

import mazestormer.remote.Communicator;
import mazestormer.remote.Message;
import mazestormer.remote.MessageListener;
import mazestormer.remote.RequestMessage;
import mazestormer.util.AbstractFuture;

public abstract class RequestFuture<V> extends AbstractFuture<V> implements
		MessageListener<Message> {

	private final int requestId;
	private final Communicator<?, ? extends Message> communicator;

	public RequestFuture(RequestMessage request,
			Communicator<?, ? extends Message> communicator) {
		this.requestId = request.getRequestId();
		this.communicator = communicator;
		communicator.addListener(this);
	}

	@Override
	public void messageReceived(Message message) {
		if (!(message instanceof RequestMessage))
			return;

		RequestMessage response = (RequestMessage) message;
		if (response.getRequestId() == requestId && isResponse(response)) {
			resolve(getResponse(response));
			communicator.removeListener(this);
		}
	}

	protected abstract boolean isResponse(RequestMessage message);

	protected abstract V getResponse(RequestMessage message);

}
