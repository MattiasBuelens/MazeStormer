package mazestormer.util;


public interface FutureListener<V> {

	public void futureResolved(Future<? extends V> future);

	public void futureCancelled(Future<? extends V> future);

}
