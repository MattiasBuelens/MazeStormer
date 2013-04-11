package mazestormer.util;

import com.google.common.util.concurrent.ListenableFuture;

public interface Future<V> extends ListenableFuture<V> {

	boolean cancel();

	/**
	 * Adds a future listener.
	 * 
	 * @param listener
	 *            The future listener
	 */
	void addFutureListener(FutureListener<? super V> listener);

}
