package mazestormer.util;

import java.util.Timer;
import java.util.TimerTask;

public class WaitFuture<V> extends AbstractFuture<V> implements
		FutureListener<V> {

	private Timer timer;

	public void resolveAfter(final V value, long timeout) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				resolve(value);
			}
		}, timeout);
	}

	@Override
	public void futureResolved(Future<? extends V> future) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void futureCancelled(Future<? extends V> future) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}