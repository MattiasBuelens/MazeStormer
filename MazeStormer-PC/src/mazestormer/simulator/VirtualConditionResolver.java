package mazestormer.simulator;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import mazestormer.condition.Condition;
import mazestormer.condition.ConditionFuture;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public abstract class VirtualConditionResolver<C extends Condition, V> {

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private AtomicBoolean isTerminated = new AtomicBoolean(false);

	private final Set<Future> futures = new CopyOnWriteArraySet<Future>();

	private final ScheduledExecutorService executor;
	private ScheduledFuture<?> task;
	private final static long updateFrequency = 50; // ms

	private static final ThreadFactory factory = new ThreadFactoryBuilder()
			.setNameFormat("VirtualConditionResolver-%d").build();

	public VirtualConditionResolver() {
		executor = Executors.newSingleThreadScheduledExecutor(factory);
	}

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
		isRunning.set(true);
		// Start checking
		if (task == null) {
			task = executor.scheduleAtFixedRate(new Runner(), 0,
					updateFrequency, TimeUnit.MILLISECONDS);
		}
	}

	protected void stop() {
		isRunning.set(false);
		// Cancel any remaining futures
		for (Future future : futures) {
			future.cancel(true);
		}
		futures.clear();
		// Stop checking
		if (task != null) {
			task.cancel(false);
			task = null;
		}
	}

	protected boolean isRunning() {
		return isRunning.get();
	}

	public boolean isTerminated() {
		return isTerminated.get();
	}

	public void terminate() {
		if (!isTerminated()) {
			isTerminated.set(true);
			stop();
			executor.shutdown();
		}
	}

	protected abstract V getValue();

	protected abstract boolean matches(C condition, V value);

	private class Runner implements Runnable {

		@Override
		public void run() {
			if (!isRunning())
				return;

			V value = getValue();

			// Update all futures
			Iterator<Future> it = futures.iterator();
			while (it.hasNext()) {
				Future future = it.next();
				future.check(value);
			}

			// Stop if no more futures to check
			if (futures.isEmpty()) {
				stop();
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
		protected void resolve() {
			// Remove when resolved
			removeFuture(this);
			super.resolve();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			// Remove when cancelled
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
