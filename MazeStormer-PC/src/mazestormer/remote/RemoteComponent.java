package mazestormer.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mazestormer.command.Command;
import mazestormer.report.Report;

public abstract class RemoteComponent {

	private final Communicator<Command, Report> communicator;
	private List<MessageListener<Report>> messageListeners = new ArrayList<MessageListener<Report>>();

	public RemoteComponent(Communicator<Command, Report> communicator) {
		this.communicator = communicator;
	}

	protected Communicator<Command, Report> getCommunicator() {
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
	 * Adds a message listener.
	 * 
	 * @param listener
	 *            The message listener.
	 */
	protected void addMessageListener(MessageListener<Report> listener) {
		// Add and store message listener
		messageListeners.add(listener);
		communicator.addListener(listener);
	}

	/**
	 * Terminates this remote component.
	 */
	public void terminate() {
		// Remove registered message listeners
		for (MessageListener<Report> listener : messageListeners) {
			communicator.removeListener(listener);
		}
	}
}
