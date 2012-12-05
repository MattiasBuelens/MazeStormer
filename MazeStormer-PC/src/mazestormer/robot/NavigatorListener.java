package mazestormer.robot;

import mazestormer.robot.Navigator.State;

public interface NavigatorListener {

	public void navigatorStep(State state);

	public void navigatorStepped(State state);

	public void navigatorPaused(State state);

}
