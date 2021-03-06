package mazestormer.physical;

import java.util.ArrayList;
import java.util.List;

import mazestormer.command.CommandType;
import mazestormer.command.ConditionalCommand;
import mazestormer.command.ConditionalCommandBuilder;
import mazestormer.command.ConditionalCommandBuilder.CommandBuilder;
import mazestormer.command.RotateCommand;
import mazestormer.command.StopCommand;
import mazestormer.command.TravelCommand;
import mazestormer.condition.Condition;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public class PhysicalCommandBuilder extends ReportRequester<Void> implements ConditionalCommandBuilder.CommandBuilder {

	private final ConditionalCommand command;
	private final List<Runnable> actions = new ArrayList<Runnable>();

	public PhysicalCommandBuilder(PhysicalCommunicator communicator, CommandType type, Condition condition) {
		super(communicator);
		command = new ConditionalCommand(type, condition);
		command.setRequestId(communicator.nextRequestId());
	}

	@Override
	public Future<Void> build() {
		Future<Void> future = request(command);
		future.addFutureListener(new Listener(future));
		return future;
	}

	protected void trigger() {
		for (Runnable action : actions) {
			action.run();
		}
	}

	@Override
	public CommandBuilder run(Runnable action) {
		actions.add(action);
		return this;
	}

	@Override
	public CommandBuilder forward() {
		return travel(Double.POSITIVE_INFINITY);
	}

	@Override
	public CommandBuilder backward() {
		return travel(Double.NEGATIVE_INFINITY);
	}

	@Override
	public CommandBuilder travel(double distance) {
		command.addCommand(new TravelCommand(CommandType.TRAVEL, distance));
		return this;
	}

	@Override
	public CommandBuilder rotateLeft() {
		return rotate(Double.POSITIVE_INFINITY);
	}

	@Override
	public CommandBuilder rotateRight() {
		return rotate(Double.NEGATIVE_INFINITY);
	}

	@Override
	public CommandBuilder rotate(double angle) {
		command.addCommand(new RotateCommand(CommandType.ROTATE, angle));
		return this;
	}

	@Override
	public CommandBuilder stop() {
		command.addCommand(new StopCommand(CommandType.STOP));
		return this;
	}

	private class Listener implements FutureListener<Void> {

		private final Future<Void> future;

		public Listener(Future<Void> future) {
			this.future = future;
		}

		@Override
		public void futureResolved(Future<? extends Void> future, Void result) {
			if (future == this.future) {
				trigger();
			}
		}

		@Override
		public void futureCancelled(Future<? extends Void> future) {
		}

	}

}
