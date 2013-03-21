package mazestormer.state;

public interface StateListener<S extends State<?, ?>> {

	public void stateStarted();

	public void stateStopped();

	public void stateFinished();

	public void statePaused(S currentState, boolean onTransition);

	public void stateResumed(S currentState);

	public void stateTransitioned(S nextState);

}
