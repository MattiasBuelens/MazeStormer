package mazestormer.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import mazestormer.util.AbstractFuture;
import mazestormer.util.CancellationException;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public abstract class Runner implements RunnerTask, RunnerListener,
		FutureListener<Void> {

	private final Pilot pilot;
	private final ExecutorService executor;
	private final List<RunnerListener> listeners = new ArrayList<RunnerListener>();

	private RunnerFuture future;

	public Runner(Pilot pilot) {
		this.pilot = pilot;
		addListener(this);

		// Named executor
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(
				getClass().getSimpleName() + "-%d").build();
		executor = Executors.newSingleThreadExecutor(factory);
	}

	protected Pilot getPilot() {
		return pilot;
	}

	/**
	 * Start this runner.
	 * 
	 * @return True if the runner is started, false if it was already running.
	 */
	public boolean start() {
		if (isRunning())
			return false;
		start(this);
		return true;
	}

	public void restart() {
		cancel();
		start();
	}

	protected void start(Runnable task) {
		startTask(prepare(task));
	}

	protected void start(RunnerTask task) {
		startTask(prepare(task));
	}

	private void startTask(Runnable task) {
		future = new RunnerFuture();
		future.addFutureListener(this);

		executor.execute(task);

		for (RunnerListener listener : listeners) {
			listener.onStarted();
		}
	}

	/**
	 * Cancel this runner.
	 * 
	 * @return True if the runner is cancelled, false if it was not running.
	 */
	public boolean cancel() {
		if (!isRunning())
			return false;

		return future.cancel(true);
	}

	public synchronized boolean isRunning() {
		return future != null && !future.isDone();
	}

	protected void resolve() {
		if (future != null) {
			future.resolve();
		}
	}

	public void addListener(RunnerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(RunnerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void futureResolved(Future<Void> future) {
		for (RunnerListener listener : listeners) {
			listener.onCompleted();
		}
	}

	@Override
	public void futureCancelled(Future<Void> future) {
		for (RunnerListener listener : listeners) {
			listener.onCancelled();
		}
	}

	/**
	 * Subclasses should override this method to perform additional
	 * initializations.
	 */
	@Override
	public void onStarted() {
	}

	/**
	 * Subclasses should override this method to perform additional actions on
	 * completion.
	 */
	@Override
	public void onCompleted() {
		// getPilot().stop();
	}

	/**
	 * Subclasses should override this method to perform additional cleanup.
	 */
	@Override
	public void onCancelled() {
		// getPilot().stop();
	}

	/**
	 * Prepare a task to execute in this runner.
	 * 
	 * @param task
	 *            The task to wrap.
	 */
	protected Runnable prepare(final Runnable task) {
		return new Runnable() {
			@Override
			public void run() {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						task.run();
					}
				});
			}
		};
	}

	/**
	 * Prepare a task to execute in this runner and catch cancellations.
	 * 
	 * @param task
	 *            The task to wrap.
	 */
	protected Runnable prepare(final RunnerTask task) {
		return prepare(new Runnable() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (CancellationException e) {
					onCancelled();
				}
			}
		});
	}

	private class RunnerFuture extends AbstractFuture<Void> {

		protected boolean resolve() {
			return resolve(null);
		}

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
