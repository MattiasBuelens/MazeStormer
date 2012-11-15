package mazestormer.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mazestormer.condition.Condition;
import mazestormer.remote.Communicator;
import mazestormer.remote.MessageListener;
import mazestormer.report.Report;

@SuppressWarnings("deprecation")
public class ConditionalCommandListener implements MessageListener<Command>,
		ConditionResolveListener {

	private final Communicator<? extends Report, ? super Command> communicator;
	private Map<Condition, ConditionalCommand> commands = new HashMap<Condition, ConditionalCommand>();
	private List<ConditionResolver> resolvers = new ArrayList<ConditionResolver>();

	public ConditionalCommandListener(
			Communicator<? extends Report, ? super Command> communicator) {
		this.communicator = communicator;
	}

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof ConditionalCommand))
			return;

		ConditionalCommand condCommand = (ConditionalCommand) command;
		Condition condition = condCommand.getCondition();
		if (condCommand.getType() == CommandType.WHEN) {
			register(condCommand.getCondition());
			commands.put(condition, condCommand);
		}
	}

	private void register(Condition condition) {
		for (ConditionResolver resolver : resolvers) {
			resolver.register(condition);
		}
	}

	private void cancel(Condition condition) {
		for (ConditionResolver resolver : resolvers) {
			resolver.cancel(condition);
		}
		commands.remove(condition);
	}

	@Override
	public void conditionResolved(Condition condition) {
		ConditionalCommand condCommand = commands.get(condition);
		for (Command command : condCommand.getCommands()) {
			communicator.trigger(command);
		}
		commands.remove(condCommand);
	}
}
