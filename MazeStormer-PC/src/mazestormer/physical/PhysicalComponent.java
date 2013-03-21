package mazestormer.physical;

import java.util.ArrayList;
import java.util.List;

import mazestormer.command.Command;
import mazestormer.remote.MessageListener;
import mazestormer.remote.MessageSender;
import mazestormer.report.Report;

public abstract class PhysicalComponent extends MessageSender<Command> {

	private List<MessageListener<Report<?>>> messageListeners = new ArrayList<MessageListener<Report<?>>>();

	public PhysicalComponent(PhysicalCommunicator communicator) {
		super(communicator);
	}

	@Override
	public PhysicalCommunicator getCommunicator() {
		return (PhysicalCommunicator) super.getCommunicator();
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
		getCommunicator().addListener(listener);
	}

	/**
	 * Terminate this component.
	 */
	public void terminate() {
		// Remove registered message listeners
		for (MessageListener<Report<?>> listener : messageListeners) {
			getCommunicator().removeListener(listener);
		}
	}

}
