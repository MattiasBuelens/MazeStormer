package mazestormer.util;

public interface FutureListener<V> {

	public void futureResolved(Future<? extends V> future, V result);

	public void futureCancelled(Future<? extends V> future);

}
