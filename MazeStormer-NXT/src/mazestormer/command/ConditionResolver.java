package mazestormer.command;

import java.util.ArrayList;
import java.util.List;

import mazestormer.condition.Condition;

public abstract class ConditionResolver {

	private List<ConditionResolveListener> listeners = new ArrayList<ConditionResolveListener>();

	public abstract void register(Condition condition);

	public abstract void cancel(Condition condition);

	protected void resolve(Condition condition) {
		for (ConditionResolveListener listener : listeners) {
			listener.conditionResolved(condition);
		}
	}

}
