package mazestormer.robot;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import lejos.robotics.pathfinding.Path;
import mazestormer.state.State;
import mazestormer.state.StateListener;
import mazestormer.state.StateMachine;

public class Navigator extends StateMachine<Navigator, Navigator.NavigatorState> implements
		StateListener<Navigator.NavigatorState>, WaypointListener {

	private final Pilot pilot;
	private final PoseProvider poseProvider;

	private List<Waypoint> path;
	private List<NavigatorListener> listeners = new ArrayList<NavigatorListener>();

	private volatile Waypoint node;

	public Navigator(Pilot pilot, PoseProvider poseProvider) {
		this.pilot = checkNotNull(pilot);
		this.poseProvider = checkNotNull(poseProvider);
		addStateListener(this);
	}

	public Waypoint getCurrentTarget() {
		return isRunning() ? node : null;
	}

	public List<Waypoint> getRemainingPath() {
		return Collections.unmodifiableList(this.path);
	}

	public void setPath(List<Waypoint> path) {
		if (isRunning()) {
			throw new IllegalStateException("Cannot change path while traversing.");
		}
		this.path = checkNotNull(path);
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
	 * Add a navigator listener that is informed when the robot stops or reaches
	 * a way point.
	 * 
	 * @param listener
	 *            The new navigator listener.
	 */
	public void addNavigatorListener(NavigatorListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a registered navigator listener.
	 * 
	 * @param listener
	 *            The navigator listener.
	 */
	public void removeNavigatorListener(NavigatorListener listener) {
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
	 * Rotate towards target.
	 */
	protected void rotateToNode() {
		Pose pose = poseProvider.getPose();
		double bearing = pose.relativeBearing(node);
		bindTransition(pilot.rotateComplete(bearing), NavigatorState.TRAVEL);
	}

	/**
	 * Travel towards target.
	 */
	protected void travel() {
		Pose pose = poseProvider.getPose();
		double distance = pose.distanceTo(node);
		bindTransition(pilot.travelComplete(distance), NavigatorState.ROTATE_ON);
	}

	/**
	 * Rotate on target.
	 */
	protected void rotateOnTarget() {
		if (node.isHeadingRequired()) {
			Pose pose = poseProvider.getPose();
			double heading = node.getHeading() - pose.getHeading();
			bindTransition(pilot.rotateComplete(heading), NavigatorState.REPORT);
		} else {
			transition(NavigatorState.REPORT);
		}
	}

	/**
	 * Report target reached and get next node.
	 */
	protected void report() {
		if (pathCompleted()) {
			// Finished
			finish();
			return;
		}

		// Report
		reportAtWaypoint();

		transition(NavigatorState.NEXT);
	}

	/**
	 * Report target reached and get next node.
	 */
	protected void next() {
		// Next node
		node = path.remove(0);
		transition(NavigatorState.ROTATE_TO);
	}

	/*
	 * Events
	 */

	private void reportStarted() {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorStarted(pose);
		}
	}

	private void reportStopped() {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorStopped(pose);
		}
	}

	private void reportPaused(NavigatorState currentState, boolean onTransition) {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorPaused(currentState, pose, onTransition);
		}
	}

	private void reportResumed(NavigatorState currentState) {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorResumed(currentState, pose);
		}
	}

	private void reportAtWaypoint() {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorAtWaypoint(node, pose);
		}
	}

	private void reportComplete() {
		Pose pose = poseProvider.getPose();
		for (NavigatorListener l : listeners) {
			l.navigatorAtWaypoint(node, pose);
			l.navigatorCompleted(node, pose);
		}
	}

	@Override
	public void stateStarted() {
		// Report
		reportStarted();

		if (pathCompleted()) {
			// Empty path
			finish();
		} else {
			// Get the first node in the path
			node = path.remove(0);
			// Start traversing
			transition(NavigatorState.ROTATE_TO);
		}
	}

	@Override
	public void stateStopped() {
		reportStopped();
	}

	@Override
	public void stateFinished() {
		// Stop
		stop();
		// Report
		reportComplete();
	}

	@Override
	public void statePaused(NavigatorState currentState, boolean onTransition) {
		reportPaused((NavigatorState) currentState, onTransition);
	}

	@Override
	public void stateResumed(NavigatorState currentState) {
		reportResumed((NavigatorState) currentState);
	}

	@Override
	public void stateTransitioned(NavigatorState nextState) {
	}

	/*
	 * States
	 */

	public enum NavigatorState implements State<Navigator, NavigatorState> {

		// Interruptible
		ROTATE_TO {
			@Override
			public void execute(Navigator navigator) {
				navigator.rotateToNode();
			}
		},

		// Interruptible
		TRAVEL {
			@Override
			public void execute(Navigator navigator) {
				navigator.travel();
			}
		},

		// Interruptible
		ROTATE_ON {
			@Override
			public void execute(Navigator navigator) {
				navigator.rotateOnTarget();
			}
		},

		// Not interruptible
		REPORT {
			@Override
			public void execute(Navigator navigator) {
				navigator.report();
			}
		},

		// Not interruptible
		NEXT {
			@Override
			public void execute(Navigator navigator) {
				navigator.next();
			}
		};

	}

}
