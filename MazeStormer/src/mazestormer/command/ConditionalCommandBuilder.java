package mazestormer.command;

public interface ConditionalCommandBuilder {

	public CommandBuilder when(ConditionSource source,
			CompareOperator operator, double value);

	public enum ConditionSource {
		LIGHT;
	}

	public enum CompareOperator {
		GREATER_THAN, SMALLER_THAN;
	}

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
