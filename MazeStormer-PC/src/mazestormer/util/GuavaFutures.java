package mazestormer.util;

import java.util.concurrent.CancellationException;

import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class GuavaFutures {

	private GuavaFutures() {
	}

	public static <V> Future<V> fromGuava(ListenableFuture<V> future) {
		return new FutureFromGuava<V>(future);
	}

	private static class FutureFromGuava<V> extends ForwardingListenableFuture<V> implements Future<V> {

		private final ListenableFuture<V> delegate;

		public FutureFromGuava(ListenableFuture<V> delegate) {
			this.delegate = delegate();
		}

		@Override
		protected ListenableFuture<V> delegate() {
			return delegate;
		}

		@Override
		public boolean cancel() {
			return delegate().cancel(false);
		}

		@Override
		public void addFutureListener(final FutureListener<? super V> listener) {
			Futures.addCallback(delegate(), new FutureCallback<V>() {
				@Override
				public void onSuccess(V result) {
					listener.futureResolved(FutureFromGuava.this);
				}

				@Override
				public void onFailure(Throwable t) {
					if (t instanceof CancellationException) {
						listener.futureCancelled(FutureFromGuava.this);
					}
				}
			});
		}

	}

}