package mazestormer.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mazestormer.command.Command;
import mazestormer.report.Report;

public abstract class RemoteComponent {

	private final RemoteCommunicator communicator;
	private List<MessageListener<Report<?>>> messageListeners = new ArrayList<MessageListener<Report<?>>>();

	public RemoteComponent(RemoteCommunicator communicator) {
		this.communicator = communicator;
	}

	protected RemoteCommunicator getCommunicator() {
		return communicator;
	}

	/**
	 * Send a command.
	 * 
	 * @param command
	 *            The command.
	 */
	protected void send(Command command) {
		try {
			getCommunicator().send(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Add a report listener.
	 * 
	 * @param listener
	 *            The report listener.
	 */
	protected void addMessageListener(MessageListener<Report<?>> listener) {
		// Add and store message listener
		messageListeners.add(listener);
		communicator.addListener(listener);
	}

	/**
	 * Terminate this component.
	 */
	public void terminate() {
		// Remove registered message listeners
		for (MessageListener<Report<?>> listener : messageListeners) {
			communicator.removeListener(listener);
		}
	}
}
