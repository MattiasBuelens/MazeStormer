package mazestormer.robot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mazestormer.util.CancellationException;

public abstract class Runner implements RunnerTask, RunnerListener {

	private final Pilot pilot;
	private boolean isRunning = false;
	private final ExecutorService executor = Executors
			.newSingleThreadExecutor();
	private final List<RunnerListener> listeners = new CopyOnWriteArrayList<RunnerListener>();

	public Runner(Pilot pilot) {
		this.pilot = pilot;
		addListener(this);
	}

	protected Pilot getPilot() {
		return pilot;
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	/**
	 * Start this runner.
	 * 
	 * @return True if the runner is started, false if it was already running.
	 */
	public boolean start() {
		if (isRunning())
			return false;

		isRunning = true;
		executor.execute(wrap(this));
		for (RunnerListener listener : listeners) {
			listener.onStarted();
		}
		return true;
	}

	/**
	 * Cancel this runner.
	 * 
	 * @return True if the runner is cancelled, false if it was not running.
	 */
	public boolean cancel() {
		if (!isRunning())
			return false;

		isRunning = false;
		for (RunnerListener listener : listeners) {
			listener.onCancelled();
		}
		return true;
	}

	public void addListener(RunnerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(RunnerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Subclasses should override this method to perform additional
	 * initializations.
	 */
	@Override
	public void onStarted() {
	}

	/**
	 * Subclasses should override this method to perform additional cleanup.
	 */
	@Override
	public void onCancelled() {
		getPilot().stop();
	}

	/**
	 * Wrap a task to catch cancellations.
	 * 
	 * @param task
	 *            The task to wrap.
	 */
	protected Runnable wrap(final RunnerTask task) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (CancellationException e) {
					onCancelled();
				}
			}
		};
	}

	protected void fork(final Runner runner) {
		addListener(new RunnerListener() {
			@Override
			public void onStarted() {
			}

			@Override
			public void onCancelled() {
				// Cancel forked runner when this runner is cancelled
				runner.cancel();
			}
		});
	}

	protected void join(final Runner runner, final Runnable after) {
		runner.addListener(new RunnerListener() {
			@Override
			public void onStarted() {
			}

			@Override
			public void onCancelled() {
				// Continue with given task when other runner is cancelled
				executor.execute(after);
			}
		});
	}

	protected void throwWhenCancelled() throws CancellationException {
		if (!isRunning()) {
			throw new CancellationException();
		}
	}

	protected void forward() throws CancellationException {
		throwWhenCancelled();
		getPilot().forward();
	}

	protected void backward() throws CancellationException {
		throwWhenCancelled();
		getPilot().backward();
	}

	protected void travel(double distance) throws CancellationException {
		travel(distance, false);
	}

	protected void travel(double distance, boolean immediateReturn)
			throws CancellationException {
		throwWhenCancelled();
		getPilot().travel(distance, immediateReturn);
		throwWhenCancelled();
	}

	protected double getTravelSpeed() throws CancellationException {
		throwWhenCancelled();
		return getPilot().getTravelSpeed();
	}

	protected void setTravelSpeed(double speed) throws CancellationException {
		throwWhenCancelled();
		getPilot().setTravelSpeed(speed);
	}

	protected void rotateLeft() throws CancellationException {
		throwWhenCancelled();
		getPilot().rotateLeft();
	}

	protected void rotateRight() throws CancellationException {
		throwWhenCancelled();
		getPilot().rotateRight();
	}

	protected void rotate(double angle) throws CancellationException {
		rotate(angle, false);
	}

	protected void rotate(double angle, boolean immediateReturn)
			throws CancellationException {
		throwWhenCancelled();
		getPilot().rotate(angle, immediateReturn);
		throwWhenCancelled();
	}

	protected double getRotateSpeed() throws CancellationException {
		throwWhenCancelled();
		return getPilot().getRotateSpeed();
	}

	protected void setRotateSpeed(double speed) throws CancellationException {
		throwWhenCancelled();
		getPilot().setRotateSpeed(speed);
	}

	protected void stop() {
		throwWhenCancelled();
		getPilot().stop();
		throwWhenCancelled();
	}

}
