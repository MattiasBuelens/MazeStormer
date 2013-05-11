package mazestormer.ui.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.gvt.GVTTreeRendererListener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class QueuedMapHandler implements MapHandler, GVTTreeRendererListener {

	private final MapCanvas canvas;
	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	private ScheduledFuture<?> task;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(factory);

	private static final ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("QueuedMapHandler-%d")
			.build();

	/**
	 * Delay in milliseconds.
	 */
	private static final long delay = 40; // 25 fps

	public QueuedMapHandler(MapCanvas canvas) {
		this.canvas = canvas;
		canvas.addGVTTreeRendererListener(this);
	}

	public void start() {
		stop();
		task = executor.scheduleWithFixedDelay(new Invoker(), 0, delay, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (task == null)
			return;

		task.cancel(false);
		task = null;
	}

	@Override
	public void requestDOMChange(Runnable request) {
		queue.add(request);
	}

	@Override
	public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
	}

	@Override
	public void gvtRenderingStarted(GVTTreeRendererEvent e) {
	}

	@Override
	public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
		start();
	}

	@Override
	public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
	}

	@Override
	public void gvtRenderingFailed(GVTTreeRendererEvent e) {
	}

	private class Invoker implements Runnable {

		@Override
		public void run() {
			// Collect work
			List<Runnable> work = new ArrayList<Runnable>();
			queue.drainTo(work);
			// Invoke and wait
			try {
				canvas.getUpdateManager().getUpdateRunnableQueue().invokeAndWait(new Worker(work));
			} catch (InterruptedException e) {
			}
		}

	}

	private class Worker implements Runnable {

		private final Collection<Runnable> tasks;

		public Worker(Collection<Runnable> tasks) {
			this.tasks = tasks;
		}

		@Override
		public void run() {
			for (Runnable task : tasks) {
				task.run();
			}
		}

	}

}