package mazestormer.robot;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.NavigationListener;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import lejos.robotics.pathfinding.Path;

public class Navigator implements WaypointListener {

	private Pilot pilot;
	private PoseProvider poseProvider;

	private Nav nav;
	private Path path = new Path();
	private Pose pose = new Pose();
	private Waypoint destination;
	private int sequenceNr;

	private ArrayList<NavigationListener> listeners = new ArrayList<NavigationListener>();

	/**
	 * If true, causes Nav.run() to break whenever way point is reached.
	 */
	private boolean singleStep = false;

	/**
	 * Set by stop(), reset by followPath(), goTo() used by Nav.run(),
	 * callListeners().
	 */
	private boolean interrupted = false;

	/**
	 * Creates a navigator controlling the given pilot and using the given pose
	 * provider for localization.
	 * 
	 * @param pilot
	 *            The pilot.
	 * @param poseProvider
	 *            The pose provider.
	 */
	public Navigator(Pilot pilot, PoseProvider poseProvider) {
		this.pilot = pilot;
		this.poseProvider = poseProvider;
		nav = new Nav();
	}

	/**
	 * Adds a NavigationListener that is informed when a the robot stops or
	 * reaches a way point.
	 * 
	 * @param listener
	 *            The new navigation listener.
	 */
	public void addNavigationListener(NavigationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Gets the pose provider in use.
	 * 
	 * @return The pose provider.
	 */
	public PoseProvider getPoseProvider() {
		return poseProvider;
	}

	/**
	 * Sets the pose provider to use.
	 * 
	 * @param poseProvider
	 *            The new pose provider.
	 */
	public void setPoseProvider(PoseProvider poseProvider) {
		this.poseProvider = poseProvider;
	}

	/**
	 * Gets the pilot being controlled.
	 * 
	 * @return The pilot.
	 */
	public Pilot getPilot() {
		return pilot;
	}

	/**
	 * Sets the path that the navigator will traverse. By default, the robot
	 * will not stop along the way. If the robot is moving when this method is
	 * called, it stops and the current path is replaced by the new one.
	 * 
	 * @param path
	 *            to be followed.
	 */
	public void setPath(Path path) {
		if (path == null)
			return;
		if (nav.isRunning())
			stop();
		this.path = path;
		singleStep = false;
		sequenceNr = 0;
	}

	/**
	 * Clears the current path. If the robot is moving when this method is
	 * called, it stops.
	 */
	public void clearPath() {
		nav.cancel();
		path.clear();
	}

	/**
	 * Gets the current path.
	 * 
	 * @return The path.
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * Starts traversing the path. This method is non-blocking.
	 * 
	 * @param path
	 *            to be followed.
	 */
	public void followPath(Path path) {
		if (path == null)
			return;
		this.path = path;
		followPath();
	}

	/**
	 * Starts the robot traversing the current path. This method is
	 * non-blocking.
	 */
	public void followPath() {
		if (path.isEmpty())
			return;
		interrupted = false;
		nav.start();
	}

	/**
	 * Controls whether the robot stops at each way point along its path;
	 * applies to the current path only. The robot will move to the next way
	 * point if you call {@link #followPath()}.
	 * 
	 * @param singleStep
	 *            If <code>true</code>, the robot stops at each way point.
	 */
	public void singleStep(boolean singleStep) {
		this.singleStep = singleStep;
	}

	/**
	 * Starts the robot moving toward the destination. If no path exists, a new
	 * one is created consisting of the destination, otherwise the destination
	 * is added to the path. This method is non-blocking, and is equivalent to
	 * <code>addWaypoint(Waypoint); followPath();</code>
	 * 
	 * @param destination
	 *            The way point to be reached.
	 */
	public void goTo(Waypoint destination) {
		if (pathCompleted())
			singleStep = false;
		addWaypoint(destination);
		followPath();
	}

	/**
	 * Starts the moving toward the destination way point created from the
	 * parameters. If no path exists, a new one is created, otherwise the new
	 * way point is added to the path. This method is non-blocking, and is
	 * equivalent to <code>addWaypoint(float x, float y); followPath();</code>
	 * 
	 * @param x
	 *            X-coordinate of the destination.
	 * @param y
	 *            Y-coordinate of the destination.
	 */
	public void goTo(float x, float y) {
		goTo(new Waypoint(x, y));
	}

	/**
	 * Starts the moving toward the destination way point created from the
	 * parameters. If no path exists, a new one is created, otherwise the new
	 * way point is added to the path. This method is non-blocking, and is
	 * equivalent to
	 * <code>addWaypoint(float x, float y, float heading); followPath();</code>
	 * 
	 * @param x
	 *            X-coordinate of the destination.
	 * @param y
	 *            X-coordinate of the destination.
	 * @param heading
	 *            Desired robot heading at arrival.
	 */
	public void goTo(float x, float y, float heading) {
		goTo(new Waypoint(x, y, heading));
	}

	/**
	 * Adds a way point to the end of the path.
	 * 
	 * @param waypoint
	 *            The way point to be added.
	 */
	public void addWaypoint(Waypoint waypoint) {
		if (pathCompleted()) {
			sequenceNr = 0;
			singleStep = false;
		}
		path.add(waypoint);
	}

	/**
	 * Constructs a new way point from the parameters and adds it to the end of
	 * the path.
	 * 
	 * @param x
	 *            X-coordinate of the way point.
	 * @param y
	 *            Y-coordinate of the way point.
	 */
	public void addWaypoint(float x, float y) {
		addWaypoint(new Waypoint(x, y));
	}

	/**
	 * Constructs a new way point from the parameters and adds it to the end of
	 * the path.
	 * 
	 * @param x
	 *            X-coordinate of the way point.
	 * @param y
	 *            Y-coordinate of the way point.
	 * @param heading
	 *            The heading of the robot when it reaches the way point.
	 */
	public void addWaypoint(float x, float y, float heading) {
		addWaypoint(new Waypoint(x, y, heading));
	}

	/**
	 * Stops the robot. The robot will resume its path traversal if you call
	 * {@link #followPath()}.
	 */
	public void stop() {
		if (nav.cancel()) {
			interrupted = true;
			callListeners();
		}
	}

	/**
	 * Gets the way point to which the robot is presently moving.
	 * 
	 * @return The way point, or null if the path is empty.
	 */
	public Waypoint getWaypoint() {
		if (path.isEmpty())
			return null;
		return path.get(0);
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
	 * Waits for the robot to stop for any reason; returns <code>true</code> if
	 * the robot stopped at the final way point of the path.
	 * 
	 * @return True if and only if the path is completed.
	 */
	public boolean waitForStop() {
		while (isMoving()) {
			Thread.yield();
		}
		return pathCompleted();
	}

	/**
	 * Checks whether the robot is moving towards a way point.
	 * 
	 * @return True if and only if the robot is moving.
	 */
	public boolean isMoving() {
		return nav.isRunning();
	}

	public void pathGenerated() {
		// Currently does nothing
	}

	private void callListeners() {
		pose = poseProvider.getPose();
		for (NavigationListener l : listeners) {
			if (interrupted) {
				l.pathInterrupted(destination, pose, sequenceNr);
			} else {
				l.atWaypoint(destination, pose, sequenceNr);
				if (pathCompleted()) {
					l.pathComplete(destination, pose, sequenceNr);
				}
			}
		}
	}

	/**
	 * Runs the thread that processes the way point queue.
	 */
	private class Nav extends Runner {

		public Nav() {
			super(Navigator.this.getPilot());
		}

		@Override
		public void run() throws CancellationException {
			// Keep stepping
			while (isRunning() && !pathCompleted()) {
				step();
			}
			// Done
			cancel();
		}

		private void step() throws CancellationException {
			destination = path.get(0);
			pose = poseProvider.getPose();

			// Rotate towards destination
			float destinationBearing = pose.relativeBearing(destination);
			rotate(destinationBearing, true);
			waitComplete();
			pose = poseProvider.getPose();

			// Travel towards destination
			float distance = pose.distanceTo(destination);
			travel(distance, true);
			waitComplete();
			pose = poseProvider.getPose();

			// Apply destination heading
			if (destination.isHeadingRequired()) {
				rotate(destination.getHeading() - pose.getHeading(), true);
				waitComplete();
				pose = poseProvider.getPose();
			}

			if (isRunning() && !pathCompleted()) {
				if (!interrupted) {
					// Presumably at way point
					path.remove(0);
					sequenceNr++;
				}
				// Stop when path completed or single step
				if (pathCompleted() || singleStep)
					cancel();
				// Call listeners
				callListeners();
			}
		}

		private void waitComplete() {
			Pilot pilot = getPilot();
			while (pilot.isMoving() && isRunning()) {
				Thread.yield();
			}
			throwWhenCancelled();
		}

	}

}
