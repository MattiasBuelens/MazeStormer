package mazestormer.detect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureDetectorAdapter;
import lejos.robotics.objectdetection.FeatureListener;

/**
 * An abstract feature detector. Subclasses only need to implement the
 * {@link #scan()} method to build a concrete detector.
 * 
 * <p>
 * This abstract implementation differs from LeJOS'
 * {@link FeatureDetectorAdapter} in that it uses a
 * {@link ScheduledExecutorService} instead of a {@link Thread} to handle the
 * periodic scanning.
 * </p>
 */
public abstract class AbstractFeatureDetector implements FeatureDetector {

	private boolean enabled = false;
	private int delay = 0;

	private ScheduledExecutorService executor;
	private Runnable scanTask = new ScanTask();
	private ScheduledFuture<?> scanTaskHandle;

	private List<FeatureListener> listeners = new ArrayList<FeatureListener>();

	/**
	 * Create a new feature detector with the given execution delay. The
	 * detector is initialized as disabled, call
	 * {@link #enableDetection(boolean)} to start detecting.
	 * 
	 * @param delay
	 *            The delay, in milliseconds.
	 */
	public AbstractFeatureDetector(int delay) {
		setDelay(delay);
		// Named executor
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(
				getClass().getSimpleName() + "-%d").build();
		executor = Executors.newSingleThreadScheduledExecutor(factory);
	}

	/**
	 * Create a new feature detector with no delay. The detector is initialized
	 * as disabled, call {@link #enableDetection(boolean)} to start detecting.
	 */
	public AbstractFeatureDetector() {
		this(0);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void enableDetection(boolean enable) {
		if (enable != this.enabled) {
			this.enabled = enable;
			if (enable) {
				// Schedule task
				scheduleTask();
			} else {
				// Cancel task
				cancelTask();
			}
		}
	}

	@Override
	public int getDelay() {
		return delay;
	}

	@Override
	public void setDelay(int delay) {
		if (delay != this.delay) {
			this.delay = delay;
			if (isEnabled()) {
				// Reschedule with new delay
				scheduleTask();
			}
		}
	}

	private void scheduleTask() {
		// Cancel task if still running
		cancelTask();
		// Reschedule task
		scanTaskHandle = executor.scheduleWithFixedDelay(scanTask, 0,
				getDelay(), TimeUnit.MILLISECONDS);
	}

	private void cancelTask() {
		if (scanTaskHandle != null) {
			scanTaskHandle.cancel(false);
		}
	}

	@Override
	public void addListener(FeatureListener l) {
		listeners.add(l);
	}

	protected void notifyListeners(Feature feature) {
		for (FeatureListener l : listeners) {
			l.featureDetected(feature, this);
		}
	}

	private class ScanTask implements Runnable {
		@Override
		public void run() {
			if (isEnabled()) {
				// Double check if still enabled
				Feature feature = scan();
				if (feature != null) {
					notifyListeners(feature);
				}
			}
		}
	}

	@Override
	public abstract Feature scan();

}
