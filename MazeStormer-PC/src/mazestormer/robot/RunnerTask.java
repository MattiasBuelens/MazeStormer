package mazestormer.robot;

import java.util.concurrent.CancellationException;

public interface RunnerTask {
	public void run() throws CancellationException;
}
