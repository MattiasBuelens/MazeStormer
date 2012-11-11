package mazestormer.command;

public interface CommandHandle {

	public long getId();

	public Command getCommand();

	public boolean isDone();

	public boolean isCanceled();

	public boolean cancel();

	public void addListener(CommandListener listener);

	public void removeListener(CommandListener listener);

}
