package mazestormer.state;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public abstract class State {

	private final AtomicReference<State> nextState = new AtomicReference<State>();
	private final Set<ConditionalLink> conditionalLinks = Collections
			.synchronizedSet(new HashSet<ConditionalLink>());

	private StateController controller;

	/**
	 * Enter this state.
	 */
	public abstract void enter();

	/**
	 * Exit this state.
	 */
	public abstract void exit();

	/**
	 * Pause this state, leaving the robot idle.
	 */
	public abstract void pause();

	/**
	 * Resume from a paused state.
	 */
	public abstract void resume();

	/**
	 * Get the state this state transitions to by default.
	 */
	public State getNextState() {
		return nextState.get();
	}

	/**
	 * Get the conditional links from this state.
	 */
	public ImmutableCollection<ConditionalLink> getConditionalLinks() {
		return ImmutableSet.copyOf(conditionalLinks);
	}

}
