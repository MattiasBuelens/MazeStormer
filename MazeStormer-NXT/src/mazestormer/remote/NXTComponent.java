package mazestormer.remote;

import java.io.IOException;

import mazestormer.command.Command;
import mazestormer.report.Report;

public abstract class NXTComponent {

	private final NXTCommunicator communicator;

	// private List<MessageListener<Command>> messageListeners = new
	// ArrayList<MessageListener<Command>>();

	public NXTComponent(NXTCommunicator communicator) {
		this.communicator = communicator;
	}

	protected NXTCommunicator getCommunicator() {
		return communicator;
	}

	/**
	 * Send a report.
	 * 
	 * @param report
	 *            The report.
	 */
	protected void send(Report<?> report) {
		try {
			getCommunicator().send(report);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Adds a command listener.
	 * 
	 * @param listener
	 *            The command listener.
	 */
	protected void addMessageListener(MessageListener<Command> listener) {
		// Add and store message listener
		// messageListeners.add(listener);
		communicator.addListener(listener);
	}

	/**
	 * Terminates this component.
	 */
	public void terminate() {
		// Remove registered message listeners
		// for (MessageListener<Command> listener : messageListeners) {
		// communicator.removeListener(listener);
		// }
	}
}
