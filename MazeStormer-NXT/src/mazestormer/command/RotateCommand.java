package mazestormer.command;

import mazestormer.remote.Factories;
import mazestormer.remote.Factory;
import mazestormer.robot.Pilot;
import mazestormer.robot.Robot;

public class RotateCommand extends NXTCommand {

	public RotateCommand() {
		setType(CommandType.ROTATE);
	}

	@Override
	public void execute(Robot robot) {
		double angle = getParameter();
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

	public static void register(Factories factories) {
		factories.register(CommandType.ROTATE, new Factory<RotateCommand>() {
			@Override
			public RotateCommand create() {
				return new RotateCommand();
			}
		});
	}

}
