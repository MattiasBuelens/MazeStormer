package mazestormer.barcode;

import mazestormer.robot.Robot;

public class RotateAction implements Action{

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
	}
}
