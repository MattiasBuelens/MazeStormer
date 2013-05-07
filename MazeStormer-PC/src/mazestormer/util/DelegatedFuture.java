package mazestormer.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DelegatedFuture<V> implements Future<V> {

	private final Future<V> delegate;

	public DelegatedFuture(Future<V> delegate) {
		this.delegate = delegate;
	}

	protected final Future<V> delegate() {
		return delegate;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return delegate().cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return delegate().isCancelled();
	}

	@Override
	public boolean isDone() {
		return delegate().isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return delegate().get();
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return delegate().get(timeout, unit);
	}

	@Override
	public boolean cancel() {
		return delegate().cancel();
	}

	@Override
	public void addFutureListener(FutureListener<? super V> listener) {
		delegate().addFutureListener(listener);
	}

	@Override
	public void addListener(Runnable listener, Executor executor) {
		delegate().addListener(listener, executor);
	}

}
