package mazestormer.util;

public class ImmediateFuture<V> extends AbstractFuture<V> {

	public ImmediateFuture(V value) {
		resolve(value);
	}

}
