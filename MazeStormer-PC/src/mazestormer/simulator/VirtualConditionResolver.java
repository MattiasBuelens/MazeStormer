package mazestormer.simulator;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;

public abstract class VirtualConditionResolver<C extends Condition, V> {

	private boolean isRunning = false;
	private boolean isTerminated = false;

	private final Set<Future> futures = new CopyOnWriteArraySet<Future>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public ConditionFuture add(C condition) {
		if (isTerminated())
			throw new IllegalStateException();

		// Create future
		Future future = new Future(condition);
		futures.add(future);

		// Start if not already running
		if (!isRunning())
			start();

		return future;
	}

	protected void removeFuture(Future future) {
		if (!isTerminated())
			futures.remove(future);
	}

	protected void start() {
		isRunning = true;
		executor.execute(new Runner());
	}

	protected void stop() {
		isRunning = false;
		executor.shutdownNow();
	}

	protected boolean isRunning() {
		return isRunning;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() {
		isTerminated = true;
		for (Future future : futures) {
			future.cancel(true);
		}
		futures.clear();
	}

	protected abstract V getValue();

	protected abstract boolean matches(C condition, V value);

	private class Runner implements Runnable {

		@Override
		public void run() {
			while (isRunning()) {
				V value = getValue();

				// Update all futures
				Iterator<Future> it = futures.iterator();
				while (it.hasNext()) {
					Future future = it.next();
					future.check(value);
					// Remove if done
					if (future.isDone())
						removeFuture(future);
				}

				// Stop if no more futures to check
				if (futures.isEmpty()) {
					stop();
				}
			}
		}

	}

	private class Future extends ConditionFuture {

		public Future(C condition) {
			super(condition);
		}

		@Override
		@SuppressWarnings("unchecked")
		public C getCondition() {
			return (C) super.getCondition();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			removeFuture(this);
			return super.cancel(mayInterruptIfRunning);
		}

		private void check(V value) {
			if (matches(getCondition(), value)) {
				resolve();
			}
		}

	}

}
