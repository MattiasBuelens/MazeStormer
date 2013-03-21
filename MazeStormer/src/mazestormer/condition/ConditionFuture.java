package mazestormer.condition;

import mazestormer.util.AbstractFuture;

public abstract class ConditionFuture extends AbstractFuture<Void> {

	private final Condition condition;

	public ConditionFuture(Condition condition) {
		this.condition = condition;
	}

	public Condition getCondition() {
		return condition;
	}

	protected void resolve() {
		resolve(null);
	}

}
