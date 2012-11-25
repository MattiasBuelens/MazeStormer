package mazestormer.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mazestormer.util.CancellationException;

public abstract class Runner implements RunnerTask {

	private final Pilot pilot;
	private boolean isRunning = false;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public Runner(Pilot pilot) {
		this.pilot = pilot;
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
		onStarted();
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
		onCancelled();
		return true;
	}

	/**
	 * Subclasses should override this method to perform additional initializations.
	 */
	public void onStarted() {
	}

	/**
	 * Subclasses should override this method to perform additional cleanup.
	 */
	public void onCancelled() {
		getPilot().stop();
	}

	/**
	 * Wrap a task to catch cancellations.
	 * 
	 * @param task
	 * 			The task to wrap.
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

	protected void travel(double distance, boolean immediateReturn) throws CancellationException {
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

	protected void rotate(double angle, boolean immediateReturn) throws CancellationException {
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
