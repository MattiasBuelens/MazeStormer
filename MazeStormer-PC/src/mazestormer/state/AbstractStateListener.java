package mazestormer.state;

public class AbstractStateListener<S extends State<?, ?>> implements
		StateListener<S> {

	@Override
	public void stateStarted() {
	}

	@Override
	public void stateStopped() {
	}

	@Override
	public void stateFinished() {
	}

	@Override
	public void statePaused(S currentState, boolean onTransition) {
	}

	@Override
	public void stateResumed(S currentState) {
	}

	@Override
	public void stateTransitioned(S nextState) {
	}

}
