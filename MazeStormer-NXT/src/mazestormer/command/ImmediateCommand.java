package mazestormer.command;

import mazestormer.robot.Robot;

public abstract class ImmediateCommand extends Command {

	public abstract void execute(Robot robot);

}
