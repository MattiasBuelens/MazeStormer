package mazestormer.command;

import mazestormer.remote.Factories;
import mazestormer.remote.Factory;
import mazestormer.robot.Robot;

public class StopCommand extends NXTCommand {

	public StopCommand() {
		setType(CommandType.STOP);
	}

	@Override
	public void execute(Robot robot) {
		robot.getPilot().stop();
	}

	public static void register(Factories factories) {
		factories.register(CommandType.STOP, new Factory<StopCommand>() {
			@Override
			public StopCommand create() {
				return new StopCommand();
			}
		});
	}

}
