package mazestormer.command;

import java.util.HashMap;
import java.util.Map;

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

@SuppressWarnings("deprecation")
public abstract class ConditionalCommandListener extends
		MessageSender<Report<?>> implements MessageListener<Command>,
		FutureListener<Void> {

	// Commands by request identifier
	private final Map<Integer, ConditionalCommand> commandsById = new HashMap<Integer, ConditionalCommand>();

	// Bidirectional map between commands and futures
	private final Map<ConditionFuture, ConditionalCommand> commandsByFuture = new HashMap<ConditionFuture, ConditionalCommand>();
	private final Map<ConditionalCommand, ConditionFuture> futuresByCommand = new HashMap<ConditionalCommand, ConditionFuture>();

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
	 * @param command
	 *            The conditional command to execute.
	 */
	protected void resolve(ConditionalCommand command) {
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
		// Send report
		send(createReport(command));
		// Unregister
		unregister(command);
	}

	private Report<?> createReport(ConditionalCommand command) {
		RequestReport<?> report = (RequestReport<?>) ReportType.CONDITION_RESOLVED
				.build();
		report.setRequestId(command.getRequestId());
		return report;
	}

	/**
	 * Cancels the conditional command with the given request identifier.
	 * 
	 * @param requestId
	 *            The conditional command request identifier.
	 */
	protected void cancel(int requestId) {
		cancel(commandsById.get(requestId));
	}

	/**
	 * Cancels the given conditional command.
	 * 
	 * @param command
	 *            The conditional command.
	 */
	protected void cancel(ConditionalCommand command) {
		if (command == null)
			return;

		ConditionFuture future = futuresByCommand.get(command);
		if (future != null)
			future.cancel(true);
		unregister(command);
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

		commandsById.put(command.getRequestId(), command);
		commandsByFuture.put(future, command);
		futuresByCommand.put(command, future);
	}

	/**
	 * Unregisters the given conditional command.
	 * 
	 * @param command
	 *            The conditional command.
	 */
	protected void unregister(ConditionalCommand command) {
		if (command == null)
			return;

		ConditionFuture future = futuresByCommand.get(command);

		commandsById.remove(command.getRequestId());
		commandsByFuture.remove(future);
		futuresByCommand.remove(command);
	}

	@Override
	public void futureResolved(Future<? extends Void> future) {
		if (commandsByFuture.containsKey(future)) {
			resolve(commandsByFuture.get(future));
		}
	}

	@Override
	public void futureCancelled(Future<? extends Void> future) {
		if (commandsByFuture.containsKey(future)) {
			cancel(commandsByFuture.get(future));
		}
	}

}
