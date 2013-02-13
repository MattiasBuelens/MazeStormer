package mazestormer.util;

public interface Future<V> extends java.util.concurrent.Future<V> {

	boolean cancel();

	/**
	 * Adds a future listener.
	 * 
	 * @param listener
	 *            The future listener
	 */
	void addFutureListener(FutureListener<V> listener);

	/**
	 * Removes a future listener.
	 * 
	 * @param listener
	 *            The future listener
	 */
	void removeFutureListener(FutureListener<V> listener);

}
