package mazestormer.robot;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

public interface NavigatorListener {

	public void navigatorStarted(Pose pose);

	public void navigatorStopped(Pose pose);

	public void navigatorPaused(Pose pose, boolean onTransition);

	public void navigatorResumed(Pose pose);

	public void navigatorAtWaypoint(Waypoint waypoint, Pose pose);

	public void navigatorCompleted(Waypoint waypoint, Pose pose);

}
