package mazestormer.command;

import mazestormer.remote.Factories;
import mazestormer.remote.Factory;
import mazestormer.robot.Robot;

public class TravelCommand extends NXTCommand {

	public TravelCommand() {
		setType(CommandType.TRAVEL);
	}

	@Override
	public void execute(Robot robot) {
		double distance = getParameter();
		robot.getPilot().travel(distance, true);
	}

	public static void register(Factories factories) {
		factories.register(CommandType.TRAVEL, new Factory<TravelCommand>() {
			@Override
			public TravelCommand create() {
				return new TravelCommand();
			}
		});
	}

}
