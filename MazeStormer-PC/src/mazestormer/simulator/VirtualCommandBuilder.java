package mazestormer.simulator;

import java.util.ArrayList;
import java.util.List;

import mazestormer.command.ConditionalCommandBuilder;
import mazestormer.command.ConditionalCommandBuilder.CommandBuilder;
import mazestormer.condition.ConditionFuture;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public class VirtualCommandBuilder implements ConditionalCommandBuilder.CommandBuilder {

	private final ControllableRobot robot;
	private final ConditionFuture future;
	private final List<Runnable> commands = new ArrayList<Runnable>();
	private final List<Runnable> actions = new ArrayList<Runnable>();

	public VirtualCommandBuilder(ControllableRobot robot, ConditionFuture future) {
		this.robot = robot;
		this.future = future;
	}

	@Override
	public Future<Void> build() {
		future.addFutureListener(new Listener(future));
		return future;
	}

	protected void trigger() {
		for (Runnable command : commands) {
			command.run();
		}
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
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().forward();
			}
		});
		return this;
	}

	@Override
	public CommandBuilder backward() {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().backward();
			}
		});
		return this;
	}

	@Override
	public CommandBuilder travel(final double distance) {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().travel(distance, true);
			}
		});
		return this;
	}

	@Override
	public CommandBuilder rotateLeft() {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().rotateLeft();
			}
		});
		return this;
	}

	@Override
	public CommandBuilder rotateRight() {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().rotateRight();
			}
		});
		return this;
	}

	@Override
	public CommandBuilder rotate(final double angle) {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().rotate(angle, true);
			}
		});
		return this;
	}

	@Override
	public CommandBuilder stop() {
		commands.add(new Runnable() {
			@Override
			public void run() {
				robot.getPilot().stop();
			}
		});
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
