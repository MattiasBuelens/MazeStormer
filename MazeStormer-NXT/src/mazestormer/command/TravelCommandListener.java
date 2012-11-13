package mazestormer.command;

import mazestormer.remote.MessageListener;
import mazestormer.robot.Robot;

public class TravelCommandListener implements MessageListener<Command> {

	private final Robot robot;

	public TravelCommandListener(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof RotateCommand))
			return;
		double distance = ((TravelCommand) command).getDistance();
		robot.getPilot().travel(distance, true);
	}

}
