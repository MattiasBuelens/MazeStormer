package mazestormer.state;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import mazestormer.condition.Condition;
import mazestormer.robot.ControllableRobot;
import mazestormer.util.Future;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class StateController {

	private final ControllableRobot robot;
	private final State initialState;

	private final AtomicReference<State> currentState = new AtomicReference<State>();
	// private final AtomicReference<State> nextState = new
	// AtomicReference<State>();
	private final AtomicBoolean isRunning = new AtomicBoolean();
	private final AtomicBoolean isPaused = new AtomicBoolean();

	private final Map<State, State> directLinks = new ConcurrentHashMap<State, State>();
	private final SetMultimap<State, Link> conditionalLinks = Multimaps
			.synchronizedSetMultimap(HashMultimap.<State, Link> create());
	private final Set<Transitioner> conditionalTransitions = new HashSet<Transitioner>();

	public StateController(ControllableRobot robot, State initialState) {
		this.robot = checkNotNull(robot);
		this.initialState = checkNotNull(initialState);
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public boolean isPaused() {
		return isPaused.get();
	}

	public synchronized void start() {
		if (isRunning()) {
			throw new IllegalStateException("Already running.");
		}
		if (isPaused()) {
			throw new IllegalStateException("Cannot start when paused.");
		}

		isRunning.compareAndSet(false, true);
		// Enter initial state
		currentState.set(initialState);
		enter(initialState);
	}

	public synchronized void stop() {
		if (!isRunning()) {
			throw new IllegalStateException("Already stopped.");
		}
		if (isPaused()) {
			throw new IllegalStateException("Cannot start when paused.");
		}

		isRunning.compareAndSet(true, false);
		// Clear current state and pause
		pause(currentState.getAndSet(null));
		// Clear next state
		// nextState.set(null);
	}

	public synchronized void pause() {
		if (!isRunning()) {
			throw new IllegalStateException("Not running.");
		}

		isPaused.compareAndSet(false, true);
		pause(currentState.get());
	}

	/**
	 * Resume from a paused state.
	 * 
	 * <!--
	 * <p>
	 * If a next state was set with {@link #transition(State)}, that state gains
	 * control. If not, the current state is resumed.
	 * </p>
	 * -->
	 */
	public synchronized void resume() {
		if (!isRunning()) {
			throw new IllegalStateException("Not running.");
		}

		isPaused.compareAndSet(true, false);

		// State next = nextState.getAndSet(null);
		// if (next != null) {
		// Transition to next state
		// transition(next);
		// } else {
		// Resume current state
		resume(currentState.get());
		// }
	}

	/**
	 * Transition from the current state to the linked state, if any.
	 */
	public synchronized void transition() {
		if (!isRunning()) {
			throw new IllegalStateException("Not running.");
		}

		State current = currentState.get();
		State next = directLinks.get(current);
		if (next != null) {
			transition(next);
		} else {
			// No linked state, maybe report?
		}
	}

	/**
	 * Transition from the current state to the given state.
	 * 
	 * <!--
	 * <p>
	 * If currently paused, the given state is stored and is transitioned to
	 * when resumed with {@link #resume()}.
	 * </p>
	 * -->
	 * 
	 * @param newState
	 *            The new state.
	 */
	public synchronized void transition(State newState) {
		if (!isRunning()) {
			throw new IllegalStateException("Not running.");
		}

		if (isPaused()) {
			// nextState.set(newState);
		} else {
			exit(currentState.getAndSet(newState));
			enter(newState);
		}
	}

	/**
	 * Link the two given states so that the previous state transitions into the
	 * next state.
	 * 
	 * @param prevState
	 *            The previous state to transition from.
	 * @param nextState
	 *            The next state to transition to.
	 */
	public synchronized void link(State prevState, State nextState) {
		directLinks.put(checkNotNull(prevState), checkNotNull(nextState));
	}

	/**
	 * Link the two given states conditionally so that the previous state
	 * transitions into the next state when the condition is fulfilled.
	 * 
	 * @param prevState
	 *            The previous state to transition from.
	 * @param nextState
	 *            The next state to transition to.
	 * @param condition
	 *            The condition for the transition.
	 */
	public synchronized void link(State prevState, State nextState,
			Condition condition) {
		Link link = new Link(checkNotNull(prevState), checkNotNull(nextState),
				checkNotNull(condition));
		conditionalLinks.put(prevState, link);
	}

	/**
	 * Remove all links from the given state.
	 */
	public synchronized void unlink(State state) {
		checkNotNull(state);
		directLinks.remove(state);
		conditionalLinks.removeAll(state);
	}

	/**
	 * Enter the given state.
	 */
	private void enter(State state) {
		setupTransitions(state);
		state.enter();
	}

	/**
	 * Exit from the given state.
	 */
	private void exit(State state) {
		cancelTransitions();
		state.exit();
	}

	/**
	 * Pause the given state.
	 */
	private void pause(State state) {
		cancelTransitions();
		state.enter();
	}

	/**
	 * Resume from the given state.
	 */
	private void resume(State state) {
		setupTransitions(state);
		state.resume();
	}

	/**
	 * Setup conditional transitions for the new state. Called when entering a
	 * new state.
	 */
	private void setupTransitions(State state) {
		// Create conditional transitions
		synchronized (conditionalTransitions) {
			conditionalTransitions.clear();
			Set<Link> links = conditionalLinks.get(state);
			synchronized (links) {
				for (Link link : links) {
					conditionalTransitions.add(new Transitioner(link));
				}
			}
		}
	}

	/**
	 * Cancel conditional transitions. Called when exiting a state.
	 */
	private void cancelTransitions() {
		// Cancel conditional transitions
		synchronized (conditionalTransitions) {
			for (Transitioner transitioner : conditionalTransitions) {
				transitioner.cancel();
			}
			conditionalTransitions.clear();
		}
	}

	private static class Link {

		private final State prevState;
		private final State nextState;
		private final Condition condition;

		public Link(State prevState, State nextState, Condition condition) {
			this.prevState = prevState;
			this.nextState = nextState;
			this.condition = condition;
		}

		public State getPreviousState() {
			return prevState;
		}

		public State getNextState() {
			return nextState;
		}

		public Condition getCondition() {
			return condition;
		}

	}

	private class Transitioner implements Runnable {

		private final Link link;
		private final Future<Void> handle;

		public Transitioner(Link link) {
			this.link = link;
			this.handle = robot.when(link.getCondition()).run(this).build();
		}

		@Override
		public void run() {
			// Transition when in correct previous state
			if (link.getPreviousState().equals(currentState.get())) {
				transition(link.getNextState());
			}
		}

		public void cancel() {
			handle.cancel();
		}

	}

}
