package mazestormer.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;
import mazestormer.remote.MessageListener;
import mazestormer.remote.MessageSender;
import mazestormer.remote.NXTCommunicator;
import mazestormer.report.Report;
import mazestormer.report.ReportType;
import mazestormer.report.RequestReport;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public abstract class ConditionalCommandListener extends MessageSender<Report<?>> implements MessageListener<Command>,
		FutureListener<Void> {

	private final List<Entry> entries = new ArrayList<Entry>();

	public ConditionalCommandListener(NXTCommunicator communicator) {
		super(communicator);
	}

	@Override
	public NXTCommunicator getCommunicator() {
		return (NXTCommunicator) super.getCommunicator();
	}

	/**
	 * Creates a future which is resolved when the given condition is fulfilled.
	 * 
	 * @param condition
	 *            The condition.
	 * 
	 * @return A condition future, or null if this listener should ignore the
	 *         given condition.
	 */
	public abstract ConditionFuture createFuture(Condition condition);

	@Override
	public void messageReceived(Command command) {
		if (command instanceof ConditionalCommand) {
			// Register new conditional command
			register((ConditionalCommand) command);
		} else if (command instanceof CancelRequestCommand) {
			// Cancel conditional command
			cancel(((CancelRequestCommand) command).getRequestId());
		}
	}

	/**
	 * Resolves a conditional command.
	 * 
	 * <p>
	 * Executes the linked commands, sends a condition report and unregisters
	 * the conditional command.
	 * </p>
	 * 
	 * @param index
	 *            The entry index of the command to execute.
	 */
	protected void resolve(int index) {
		if (index < 0 || index >= entries.size())
			return;

		Entry entry = entries.get(index);
		ConditionalCommand command = entry.getCommand();

		// Send report
		send(createReport(command));
		// Execute linked commands
		switch (command.getType()) {
		case WHEN:
			for (Command c : command.getCommands()) {
				getCommunicator().trigger(c);
			}
			break;
		default:
			break;
		}
		// Unregister
		unregisterInternal(index);
	}

	private Report<?> createReport(ConditionalCommand command) {
		RequestReport<?> report = (RequestReport<?>) ReportType.CONDITION_RESOLVED.build();
		report.setRequestId(command.getRequestId());
		return report;
	}

	/**
	 * Cancels the conditional command with the given request identifier.
	 * 
	 * @param requestId
	 *            The conditional command request identifier.
	 */
	private void cancel(int requestId) {
		cancelInternal(findById(requestId));
	}

	/**
	 * Cancels the given conditional command.
	 * 
	 * @param index
	 *            The entry index of the conditional command.
	 */
	private void cancelInternal(int index) {
		if (index < 0 || index >= entries.size())
			return;

		Entry entry = entries.get(index);
		ConditionFuture future = entry.getFuture();
		if (future != null)
			future.cancel();
		unregisterInternal(index);
	}

	/**
	 * Attempts to register the given conditional command.
	 * 
	 * @param command
	 *            The conditional command.
	 */
	protected void register(ConditionalCommand command) {
		if (command == null)
			return;

		// Attempt to create future
		ConditionFuture future = createFuture(command.getCondition());
		if (future == null)
			return;
		future.addFutureListener(this);

		// Add
		Entry entry = new Entry(future, command);
		entries.add(entry);
	}

	/**
	 * Unregisters the given conditional command.
	 * 
	 * @param command
	 *            The conditional command.
	 */
	protected void unregister(ConditionalCommand command) {
		unregisterInternal(findByCommand(command));
	}

	protected void unregisterInternal(int index) {
		if (index < 0 || index >= entries.size())
			return;

		entries.remove(index);
	}

	@Override
	public void futureResolved(Future<? extends Void> future, Void result) {
		resolve(findByFuture(future));
	}

	@Override
	public void futureCancelled(Future<? extends Void> future) {
		cancelInternal(findByFuture(future));
	}

	private int findById(int requestId) {
		ListIterator<Entry> it = entries.listIterator();
		while (it.hasNext()) {
			int i = it.nextIndex();
			Entry entry = it.next();
			if (entry.getCommand().getRequestId() == requestId) {
				return i;
			}
		}
		return -1;
	}

	private int findByCommand(ConditionalCommand command) {
		ListIterator<Entry> it = entries.listIterator();
		while (it.hasNext()) {
			int i = it.nextIndex();
			Entry entry = it.next();
			if (entry.getCommand().equals(command)) {
				return i;
			}
		}
		return -1;
	}

	private int findByFuture(Future<?> future) {
		ListIterator<Entry> it = entries.listIterator();
		while (it.hasNext()) {
			int i = it.nextIndex();
			Entry entry = it.next();
			if (entry.getFuture().equals(future)) {
				return i;
			}
		}
		return -1;
	}

	private static class Entry {

		private final ConditionFuture future;
		private final ConditionalCommand command;

		public Entry(ConditionFuture future, ConditionalCommand command) {
			this.future = future;
			this.command = command;
		}

		public ConditionFuture getFuture() {
			return future;
		}

		public ConditionalCommand getCommand() {
			return command;
		}

	}

}
