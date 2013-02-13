package mazestormer.command;

import mazestormer.condition.Condition;
import mazestormer.util.Future;

public interface ConditionalCommandBuilder {

	public CommandBuilder when(Condition condition);

	public interface CommandBuilder {

		public Future<Void> build();

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
