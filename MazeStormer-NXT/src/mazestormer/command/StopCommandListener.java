package mazestormer.command;

import mazestormer.remote.MessageListener;
import mazestormer.robot.Robot;

public class StopCommandListener implements MessageListener<Command> {

	private final Robot robot;

	public StopCommandListener(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof StopCommand))
			return;

		robot.getPilot().stop();
	}

}