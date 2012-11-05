package mazestormer.barcode;

import mazestormer.robot.Robot;

public class RotateClockwiseAction implements IAction{

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
		robot.getPilot().rotate(-360);
	}
}
