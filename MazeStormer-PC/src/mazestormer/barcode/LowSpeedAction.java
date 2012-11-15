package mazestormer.barcode;

import mazestormer.robot.Robot;

public class LowSpeedAction implements IAction{
	
	private static final double SPEED = 5; 

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
		robot.getPilot().setTravelSpeed(SPEED);
	}
}
