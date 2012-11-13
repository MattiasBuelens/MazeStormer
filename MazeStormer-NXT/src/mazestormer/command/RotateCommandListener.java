package mazestormer.command;

import mazestormer.remote.MessageListener;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class RotateCommandListener implements MessageListener<Command> {

	private final Robot robot;

	public RotateCommandListener(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void messageReceived(Command command) {
		if (!(command instanceof RotateCommand))
			return;

		double angle = ((RotateCommand) command).getAngle();
		Pilot pilot = robot.getPilot();

		if (Double.isInfinite(angle)) {
			if (angle > 0) {
				pilot.rotateLeft();
			} else {
				pilot.rotateRight();
			}
		} else {
			pilot.rotate(angle, true);
		}
	}

}
