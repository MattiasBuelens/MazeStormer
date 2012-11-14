package mazestormer.remote;

public abstract class MessageReplier<S extends Message, R extends Message>
		extends MessageSender<S> implements MessageListener<R> {

	public MessageReplier(Communicator<? super S, ? extends R> communicator) {
		super(communicator);
	}

}
