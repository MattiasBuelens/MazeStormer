package mazestormer.command;

import mazestormer.remote.MessageListener;
import mazestormer.robot.Robot;

public class ShutdownCommandListener implements MessageListener<Command> {

	private final Robot robot;

	public ShutdownCommandListener(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof RotateCommand))
			return;

		robot.terminate();
	}

}
