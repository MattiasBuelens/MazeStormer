package mazestormer.state;

import mazestormer.condition.Condition;

public class ConditionalLink {

	private final State prevState;
	private final State nextState;
	private final Condition condition;

	public ConditionalLink(State prevState, State nextState, Condition condition) {
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