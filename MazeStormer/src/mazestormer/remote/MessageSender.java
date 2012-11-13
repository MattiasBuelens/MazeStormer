package mazestormer.remote;

import java.io.IOException;

public abstract class MessageSender<M extends Message> {

	private final Communicator<? super M, ?> communicator;

	public MessageSender(Communicator<? super M, ?> communicator) {
		this.communicator = communicator;
	}

	protected void report(M message) {
		if (communicator.isListening()) {
			try {
				communicator.send(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

}
