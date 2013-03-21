package mazestormer.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import lejos.nxt.comm.NXTConnection;
import mazestormer.command.Command;
import mazestormer.command.CommandReader;
import mazestormer.report.Report;

public class NXTCommunicator extends Communicator<Report<?>, Command> {

	private NXTConnection connection;
	private final List<MessageListener<? super Command>> listeners;

	public NXTCommunicator(NXTConnection connection) {
		super(connection.openInputStream(), connection.openOutputStream(),
				new CommandReader());
		this.connection = connection;
		this.listeners = new ArrayList<MessageListener<? super Command>>(32);
	}

	@Override
	public void terminate() throws IOException {
		super.terminate();
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * All listeners need to be defined <strong>before</strong> starting the
	 * communicator. Adding additional listeners when receiving new messages
	 * will result in {@link #trigger(Command)} throwing a
	 * {@link ConcurrentModificationException}.
	 * </p>
	 */
	@Override
	public void addListener(MessageListener<? super Command> listener) {
		super.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * All listeners need to be defined <strong>before</strong> starting the
	 * communicator. Removing registered listeners when receiving new messages
	 * will result in {@link #trigger(Command)} throwing a
	 * {@link ConcurrentModificationException}.
	 * </p>
	 */
	@Override
	public void removeListener(MessageListener<? super Command> listener) {
		super.removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * All listeners need to be defined <strong>before</strong> starting the
	 * communicator. Changing the list of registered listeners will receiving
	 * new messages may result in a {@link ConcurrentModificationException}.
	 * </p>
	 */
	@Override
	public void trigger(final Command command) {
		super.trigger(command);
	}

	@Override
	protected List<MessageListener<? super Command>> getListeners() {
		return listeners;
	}

}
