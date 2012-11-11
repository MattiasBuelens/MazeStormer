package mazestormer.remote;

import java.util.EnumMap;
import java.util.Map;

import mazestormer.command.Command;
import mazestormer.command.CommandType;
import mazestormer.condition.Condition;
import mazestormer.condition.ConditionType;
import mazestormer.report.Report;
import mazestormer.report.ReportType;

public class Factories {

	private Map<CommandType, Factory<? extends Command>> commands = new EnumMap<CommandType, Factory<? extends Command>>(
			CommandType.class);
	private Map<ConditionType, Factory<? extends Condition>> conditions = new EnumMap<ConditionType, Factory<? extends Condition>>(
			ConditionType.class);
	private Map<ReportType, Factory<? extends Report>> reports = new EnumMap<ReportType, Factory<? extends Report>>(
			ReportType.class);

	public Factory<? extends Command> get(CommandType type) {
		return commands.get(type);
	}

	public Factory<? extends Condition> get(ConditionType type) {
		return conditions.get(type);
	}

	public Factory<? extends Report> get(ReportType type) {
		return reports.get(type);
	}

	public void register(CommandType type, Factory<? extends Command> factory) {
		commands.put(type, factory);
	}

	public void register(ConditionType type,
			Factory<? extends Condition> factory) {
		conditions.put(type, factory);
	}

	public void register(ReportType type, Factory<? extends Report> factory) {
		reports.put(type, factory);
	}

}
