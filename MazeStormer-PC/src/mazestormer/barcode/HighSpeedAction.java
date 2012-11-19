package mazestormer.barcode;

import mazestormer.robot.Robot;

public class HighSpeedAction implements IAction{
	
	private static final double SPEED = 20; 

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
		robot.getPilot().setTravelSpeed(SPEED);
	}
}
