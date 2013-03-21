package mazestormer.remote;

import java.io.IOException;

public abstract class MessageSender<M extends Message> {

	private final Communicator<? super M, ?> communicator;

	public MessageSender(Communicator<? super M, ?> communicator) {
		this.communicator = communicator;
	}

	public Communicator<? super M, ?> getCommunicator() {
		return communicator;
	}

	protected void send(M message) {
		try {
			communicator.send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

}
