package mazestormer.util;


public interface FutureListener<V> {

	public void futureResolved(Future<V> future);

	public void futureCancelled(Future<V> future);

}
