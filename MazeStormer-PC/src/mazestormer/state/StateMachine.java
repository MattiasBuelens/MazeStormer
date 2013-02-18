package mazestormer.state;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public abstract class StateMachine<M extends StateMachine<M, S>, S extends State<M, S>> {

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private AtomicBoolean isPaused = new AtomicBoolean(false);

	private volatile S currentState;
	private S pauseState;
	private volatile Future<?> nextTransition;

	private List<StateListener<S>> listeners = new ArrayList<StateListener<S>>();

	/**
	 * Get the current state of this state machine.
	 */
	public S getState() {
		return currentState;
	}

	/**
	 * Check whether this state machine is currently running.
	 */
	public boolean isRunning() {
		return isRunning.get();
	}

	/**
	 * Check whether this state machine is currently paused.
	 */
	public boolean isPaused() {
		return isPaused.get();
	}

	/**
	 * Start this state machine.
	 */
	public void start() {
		if (!isRunning()) {
			isRunning.set(true);
			reportStarted();
			setup();
		}
	}

	/**
	 * Stop this state machine.
	 */
	public void stop() {
		if (isRunning()) {
			isRunning.set(false);
			isPaused.set(false);
			if (nextTransition != null)
				nextTransition.cancel();
			reportStopped();
		}
	}

	/**
	 * Pause this state machine and store the current state.
	 */
	public void pause() {
		pause(false);
	}

	private void pause(boolean onTransition) {
		if (isRunning() && !isPaused()) {
			isPaused.set(true);
			if (nextTransition != null)
				nextTransition.cancel();
			reportPaused(onTransition);
		}
	}

	/**
	 * Resume this state machine from the last state.
	 */
	public void resume() {
		if (isRunning() && isPaused()) {
			isPaused.set(false);
			reportResumed();
			// Resume current state
			currentState.execute(self());
		}
	}

	/**
	 * Pause this state machine before entering the given state.
	 * 
	 * @param pauseState
	 *            The state to pause at.
	 */
	public void pauseAt(S pauseState) {
		this.pauseState = pauseState;
	}

	/**
	 * Add a state listener that is informed of state machine events.
	 * 
	 * @param listener
	 *            The new state listener.
	 */
	protected void addStateListener(StateListener<S> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a registered state listener.
	 * 
	 * @param listener
	 *            The state listener.
	 */
	protected void removeStateListener(StateListener<S> listener) {
		listeners.remove(listener);
	}

	/*
	 * Setup and shutdown.
	 */

	/**
	 * Initialize the state machine and transition to the first state.
	 */
	protected abstract void setup();

	/*
	 * Transitions
	 */

	/**
	 * Transition to the given state.
	 * 
	 * <ul>
	 * <li>If the state machine is ordered to pause before the given state using
	 * {@link #pauseAt(State)}, it is paused.</li>
	 * <li>If the state machine is currently paused, it will resume from the
	 * given state when calling {@link #resume()}.</li>
	 * <li>If the state machine is not paused, it immediately transitions to the
	 * given state.</li>
	 * </ul>
	 * 
	 * @param nextState
	 *            The next state.
	 */
	protected void transition(S nextState) {
		if (isRunning()) {
			currentState = nextState;
			reportTransition();
			if (pauseState == currentState) {
				// Pause requested before entering new state
				pause(true);
			} else if (!isPaused()) {
				// Continue with new state
				currentState.execute(self());
			}
		}
	}

	/**
	 * Bind the given boolean future to the transition to the given state.
	 * 
	 * <p>
	 * When the given boolean future is resolved, the state machine transitions
	 * to the given state if the result is {@code true}, otherwise it pauses.
	 * </p>
	 * 
	 * @param future
	 *            The future which results in the transition.
	 * @param nextState
	 *            The next state.
	 */
	protected void bindTransition(final Future<Boolean> future,
			final S nextState) {
		future.addFutureListener(new FutureListener<Boolean>() {
			@Override
			public void futureResolved(Future<? extends Boolean> future) {
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
			public void futureCancelled(Future<? extends Boolean> future) {
				// Ignore
			}
		});
		this.nextTransition = future;
	}

	private void reportStarted() {
		for (StateListener<S> l : listeners) {
			l.stateStarted();
		}
	}

	private void reportStopped() {
		for (StateListener<S> l : listeners) {
			l.stateStopped();
		}
	}

	private void reportPaused(boolean onTransition) {
		S state = getState();
		for (StateListener<S> l : listeners) {
			l.statePaused(state, onTransition);
		}
	}

	private void reportResumed() {
		S state = getState();
		for (StateListener<S> l : listeners) {
			l.stateResumed(state);
		}
	}

	private void reportTransition() {
		S state = getState();
		for (StateListener<S> l : listeners) {
			l.stateTransitioned(state);
		}
	}

	@SuppressWarnings("unchecked")
	private M self() {
		return (M) this;
	}

}
