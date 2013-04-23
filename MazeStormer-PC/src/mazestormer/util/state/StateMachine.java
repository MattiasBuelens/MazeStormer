package mazestormer.util.state;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import mazestormer.util.AbstractFuture;
import mazestormer.util.Future;
import mazestormer.util.FutureListener;

public abstract class StateMachine<M extends StateMachine<M, S>, S extends State<M, S>> {

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private AtomicBoolean isPaused = new AtomicBoolean(false);

	private volatile S currentState;
	private S pauseState;
	private List<Future<?>> transitionBindings = new ArrayList<Future<?>>();

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
			clearBindings();
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
			clearBindings();
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
			execute(currentState);
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

	// /**
	// * Fork this state machine and join when another machine reaches a given
	// * state.
	// *
	// * <p>
	// * This state machine is paused immediately and is resumed when the other
	// * machine transitions to the given join state. If the other machine
	// doesn't
	// * reach the given state before stopping or finishing, this machine
	// remains
	// * paused.
	// * </p>
	// *
	// * <p>
	// * The returned future indicates the result of the join.
	// * </p>
	// *
	// * @param joiner
	// * The other state machine to join.
	// * @param joinState
	// * The state at which to join the other state machine.
	// */
	// public <T extends State<?, T>> Future<Boolean> forkJoin(final
	// StateMachine<?, T> joiner, final T joinState) {
	// // Resume when join state reached
	// final StateFuture<T> joinFuture = new StateFuture<T>(joinState);
	// joinFuture.addFutureListener(new FutureListener<Boolean>() {
	// @Override
	// public void futureResolved(Future<? extends Boolean> future) {
	// try {
	// if (future.get().booleanValue()) {
	// // Joined, resume parent
	// resume();
	// }
	// // Clean up
	// joiner.removeStateListener(joinFuture);
	// } catch (InterruptedException | ExecutionException cannotHappen) {
	// }
	// }
	//
	// @Override
	// public void futureCancelled(Future<? extends Boolean> future) {
	// }
	// });
	//
	// // Listen to child
	// joiner.addStateListener(joinFuture);
	// // Pause child when in join state
	// joiner.pauseAt(joinState);
	// // Pause parent
	// pause();
	//
	// return joinFuture;
	// }

	/**
	 * Add a state listener that is informed of state machine events.
	 * 
	 * @param listener
	 *            The new state listener.
	 */
	public void addStateListener(StateListener<S> listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a registered state listener.
	 * 
	 * @param listener
	 *            The state listener.
	 */
	public void removeStateListener(StateListener<S> listener) {
		listeners.remove(listener);
	}

	/*
	 * Setup and shutdown.
	 */

	/**
	 * Initialize the state machine and transition to the first state.
	 */
	protected final void setup() {
		reportStarted();
	}

	/**
	 * Finish the state machine.
	 */
	protected final void finish() {
		reportFinished();
	}

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
				execute(currentState);
			}
		}
	}

	/**
	 * Bind the given future to the transition to the given state.
	 * 
	 * <p>
	 * When the given future is resolved, the state machine transitions to the
	 * given state.
	 * </p>
	 * 
	 * <p>
	 * If the future resolves to a {@link Boolean} result, the machine
	 * transitions only if the result is {@code true}, otherwise it pauses.
	 * </p>
	 * 
	 * @param future
	 *            The future which results in the transition.
	 * @param nextState
	 *            The next state.
	 */
	protected void bindTransition(final Future<?> future, final S nextState) {
		// Register binding
		addBinding(future);
		// Listen for future completion
		future.addFutureListener(new FutureListener<Object>() {
			@Override
			public void futureResolved(Future<? extends Object> future, Object result) {
				if (result instanceof Boolean && !((Boolean) result).booleanValue()) {
					// Interrupt, retry needed
					pause();
				} else {
					// Successfully completed, transition
					transition(nextState);
				}
			}

			@Override
			public void futureCancelled(Future<? extends Object> future) {
				// Ignore
			}
		});
	}

	/**
	 * Execute the given state.
	 * 
	 * @param state
	 *            The state to execute.
	 */
	private void execute(S state) {
		// Clean up
		clearBindings();
		// Execute
		state.execute(self());
	}

	/**
	 * Register a transition binding for the current state.
	 * 
	 * @param binding
	 *            The new binding.
	 */
	private void addBinding(Future<?> binding) {
		synchronized (transitionBindings) {
			transitionBindings.add(binding);
		}
	}

	/**
	 * Clear all transition bindings bound to the current state.
	 */
	private void clearBindings() {
		synchronized (transitionBindings) {
			for (Future<?> binding : transitionBindings) {
				binding.cancel();
			}
			transitionBindings.clear();
		}
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

	private void reportFinished() {
		for (StateListener<S> l : listeners) {
			l.stateFinished();
		}
	}

	@SuppressWarnings("unchecked")
	private M self() {
		return (M) this;
	}

	public static class StateFuture<T extends State<?, ?>> extends AbstractFuture<Boolean> implements StateListener<T> {

		private final T checkState;

		public StateFuture(T checkState) {
			this.checkState = checkState;
		}

		@Override
		public void stateStarted() {
		}

		@Override
		public void stateStopped() {
			// Failed
			resolve(false);
		}

		@Override
		public void stateFinished() {
			// Failed
			resolve(false);
		}

		@Override
		public void statePaused(T currentState, boolean onTransition) {
		}

		@Override
		public void stateResumed(T currentState) {
		}

		@Override
		public void stateTransitioned(T nextState) {
			if (nextState == checkState) {
				// Success
				resolve(true);
			}
		}

	}

	public static abstract class FinishFuture<T extends State<?, ?>> extends AbstractFuture<Void> implements
			StateListener<T> {

		@Override
		public void stateStarted() {
		}

		@Override
		public void stateStopped() {
			// Failed
			cancel();
		}

		@Override
		public void stateFinished() {
			if (isFinished()) {
				// Success
				resolve(null);
			} else {
				// Failed
				cancel();
			}
		}

		@Override
		public void statePaused(T currentState, boolean onTransition) {
		}

		@Override
		public void stateResumed(T currentState) {
		}

		@Override
		public void stateTransitioned(T nextState) {
		}

		public abstract boolean isFinished();

	}

}
