package mazestormer.robot;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import mazestormer.robot.Navigator.NavigatorState;

public interface NavigatorListener {

	public void navigatorStarted(Pose pose);

	public void navigatorStopped(Pose pose);

	public void navigatorPaused(NavigatorState currentState, Pose pose,
			boolean onTransition);

	public void navigatorResumed(NavigatorState currentState, Pose pose);

	public void navigatorAtWaypoint(Waypoint waypoint, Pose pose);

	public void navigatorCompleted(Waypoint waypoint, Pose pose);

}
