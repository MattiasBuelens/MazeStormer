package mazestormer.command;

import mazestormer.condition.Condition;

public interface ConditionalCommandBuilder {

	public CommandBuilder when(Condition condition);

	public interface CommandHandle {

		public void cancel();

	}

	public interface CommandBuilder {

		public CommandHandle build();

		public CommandBuilder run(Runnable action);

		public CommandBuilder forward();

		public CommandBuilder backward();

		public CommandBuilder travel(double distance);

		public CommandBuilder rotateLeft();

		public CommandBuilder rotateRight();

		public CommandBuilder rotate(double angle);

		public CommandBuilder stop();

	}

}
