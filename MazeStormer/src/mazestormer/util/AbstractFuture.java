package mazestormer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AbstractFuture<V> implements Future<V> {

	private volatile boolean isCancelled = false;
	private volatile boolean isResolved = false;
	private volatile V result;

	private List<FutureListener<V>> listeners = new ArrayList<FutureListener<V>>();

	private synchronized boolean isResolved() {
		return isResolved;
	}

	protected synchronized boolean resolve(V result) {
		if (!isDone()) {
			this.result = result;
			isResolved = true;
			fireResolved();
		}
		return isResolved();
	}

	@Override
	public synchronized boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		if (!isCancelled()) {
			isCancelled = true;
			fireCancelled();
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean isDone() {
		return isCancelled() || isResolved();
	}

	@Override
	public V get() throws CancellationException {
		while (!isDone()) {
			Thread.yield();
		}

		if (isResolved()) {
			return result;
		} else if (isCancelled()) {
			throw new CancellationException();
		} else {
			return null;
		}
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws CancellationException, TimeoutException {
		// Start timeout timer
		TimeoutTask timeoutTask = new TimeoutTask();
		Timer timer = new Timer();
		timer.schedule(timeoutTask, unit.toMillis(timeout));

		// Wait until done or time out
		while (!isDone() && !timeoutTask.isTimeout()) {
			Thread.yield();
		}

		// Cancel timeout timer
		timer.cancel();

		if (isResolved()) {
			return result;
		} else if (isCancelled()) {
			throw new CancellationException();
		} else if (timeoutTask.isTimeout()) {
			throw new TimeoutException();
		} else {
			return null;
		}
	}

	@Override
	public void addFutureListener(FutureListener<V> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeFutureListener(FutureListener<V> listener) {
		listeners.remove(listener);
	}

	private void fireResolved() {
		for (FutureListener<V> listener : listeners) {
			listener.futureResolved(this);
		}
	}

	private void fireCancelled() {
		for (FutureListener<V> listener : listeners) {
			listener.futureCancelled(this);
		}
	}

	private class TimeoutTask extends TimerTask {

		private volatile boolean isTimeout = false;

		public boolean isTimeout() {
			return isTimeout;
		}

		@Override
		public void run() {
			isTimeout = true;
		}

	}

}
