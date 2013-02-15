package mazestormer.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.*;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import lejos.robotics.pathfinding.Path;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public class Navigator2 implements WaypointListener {

	private final Pilot pilot;
	private final PoseProvider poseProvider;

	private Path path;
	private List<NavigationListener> listeners = new ArrayList<NavigationListener>();

	private AtomicBoolean isRunning = new AtomicBoolean();
	private AtomicBoolean isPaused = new AtomicBoolean();
	private State currentState;
	private Waypoint node;
	private AtomicInteger nodeIndex = new AtomicInteger();
	private Future<?> nextTransition;

	private State pauseState;

	public Navigator2(Pilot pilot, PoseProvider poseProvider) {
		this.pilot = checkNotNull(pilot);
		this.poseProvider = checkNotNull(poseProvider);
	}

	public void setPath(Path path) {
		if (isRunning()) {
			throw new IllegalStateException(
					"Cannot change path while traversing.");
		}
		this.path = checkNotNull(path);
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public boolean isPaused() {
		return isPaused.get();
	}

	/**
	 * Start path traversal.
	 */
	public void start() {
		if (!isRunning()) {
			isRunning.set(true);
			init();
		}
	}

	/**
	 * Stop path traversal. Does <strong>not</strong> stop the pilot.
	 */
	public void stop() {
		if (isRunning()) {
			isRunning.set(false);
			isPaused.set(false);
			if (nextTransition != null)
				nextTransition.cancel();
			reportInterrupted();
		}
	}

	/**
	 * Pause path traversal. Does <strong>not</strong> stop the pilot.
	 */
	public void pause() {
		if (isRunning() && !isPaused()) {
			isPaused.set(true);
			if (nextTransition != null)
				nextTransition.cancel();
			reportInterrupted();
		}
	}

	/**
	 * Resume path traversal.
	 */
	public void resume() {
		if (isRunning() && isPaused()) {
			isPaused.set(false);
			currentState.execute(this);
		}
	}

	/**
	 * Pause this navigator before entering the given state.
	 * 
	 * @param pauseState
	 *            The state to pause at.
	 */
	public void pauseAt(State pauseState) {
		this.pauseState = pauseState;
	}

	/**
	 * Checks whether the final way point has been reached.
	 * 
	 * @return True if and only if the path is completed.
	 */
	public boolean pathCompleted() {
		return path == null || path.isEmpty();
	}

	/**
	 * Add a navigation listener that is informed when the robot stops or
	 * reaches a way point.
	 * 
	 * @param listener
	 *            The new navigation listener.
	 */
	public void addNavigationListener(NavigationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a registered navigation listener.
	 * 
	 * @param listener
	 *            The navigation listener.
	 */
	public void removeNavigationListener(NavigationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void addWaypoint(Waypoint wp) {
		if (pathCompleted()) {
			path = new Path();
		}
		path.add(wp);
	}

	@Override
	public void pathGenerated() {
		// TODO Auto-generated method stub

	}

	/*
	 * Path traversal
	 */

	/**
	 * Initialize the path traversal.
	 */
	private void init() {
		// Get the first node in the path
		node = path.remove(0);
		nodeIndex.set(0);
		// Start traversing
		transition(State.ROTATE_TO);
	}

	/**
	 * Finish the path traversal.
	 */
	private void finish() {
		// Clear state
		isRunning.set(false);
		isPaused.set(false);
		// Report
		reportComplete();
	}

	/**
	 * Rotate towards target.
	 */
	private void rotateToNode() {
		Pose pose = poseProvider.getPose();
		double bearing = pose.relativeBearing(node);
		bindTransition(pilot.rotateComplete(bearing), State.TRAVEL);
	}

	/**
	 * Travel towards target.
	 */
	private void travel() {
		Pose pose = poseProvider.getPose();
		double distance = pose.distanceTo(node);
		bindTransition(pilot.travelComplete(distance), State.ROTATE_ON);
	}

	/**
	 * Rotate on target.
	 */
	private void rotateOnTarget() {
		if (node.isHeadingRequired()) {
			Pose pose = poseProvider.getPose();
			double heading = node.getHeading() - pose.getHeading();
			bindTransition(pilot.rotateComplete(heading), State.REPORT);
		} else {
			transition(State.REPORT);
		}
	}

	/**
	 * Report target reached and get next node.
	 */
	private void report() {
		if (pathCompleted()) {
			// Finished
			finish();
			return;
		}

		// Report
		reportAtWaypoint();

		// Next node
		node = path.remove(0);
		nodeIndex.incrementAndGet();
		transition(State.ROTATE_TO);
	}

	/*
	 * Transitions
	 */

	private void transition(State nextState) {
		if (isRunning()) {
			currentState = nextState;
			if (pauseState == currentState) {
				// Pause requested before entering new state
				pause();
			} else if (!isPaused()) {
				// Continue with new state
				currentState.execute(this);
			}
		}
	}

	private void bindTransition(final Future<Boolean> future,
			final State nextState) {
		future.addFutureListener(new FutureListener<Boolean>() {
			@Override
			public void futureResolved(Future<Boolean> future) {
				try {
					if (future.get().booleanValue()) {
						// Transition when successfully completed
						transition(nextState);
					} else {
						// Interrupt, retry needed
						pause();
					}
				} catch (InterruptedException | ExecutionException cannotHappen) {
				}
			}

			@Override
			public void futureCancelled(Future<Boolean> future) {
				// Ignore
			}
		});
		this.nextTransition = future;
	}

	private void reportAtWaypoint() {
		Pose pose = poseProvider.getPose();
		int index = nodeIndex.get();
		for (NavigationListener l : listeners) {
			l.atWaypoint(node, pose, index);
		}
	}

	private void reportComplete() {
		Pose pose = poseProvider.getPose();
		int index = nodeIndex.get();
		for (NavigationListener l : listeners) {
			l.atWaypoint(node, pose, index);
			l.pathComplete(node, pose, index);
		}
	}

	private void reportInterrupted() {
		Pose pose = poseProvider.getPose();
		int index = nodeIndex.get();
		for (NavigationListener l : listeners) {
			l.pathInterrupted(node, pose, index);
		}
	}

	public enum State {

		// Interruptible
		ROTATE_TO {
			@Override
			protected void execute(Navigator2 navigator) {
				navigator.rotateToNode();
			}
		},

		// Interruptible
		TRAVEL {
			@Override
			protected void execute(Navigator2 navigator) {
				navigator.travel();
			}
		},

		// Interruptible
		ROTATE_ON {
			@Override
			protected void execute(Navigator2 navigator) {
				navigator.rotateOnTarget();
			}
		},

		// Not interruptible
		REPORT {
			@Override
			protected void execute(Navigator2 navigator) {
				navigator.report();
			}
		};

		protected abstract void execute(Navigator2 navigator);

	}

}
