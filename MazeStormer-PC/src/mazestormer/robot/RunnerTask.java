package mazestormer.robot;

import mazestormer.util.CancellationException;

public interface RunnerTask {
	public void run() throws CancellationException;
}
