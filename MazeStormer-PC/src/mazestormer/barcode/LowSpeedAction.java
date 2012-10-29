package mazestormer.barcode;

import mazestormer.robot.Robot;

public class LowSpeedAction implements IAction{
	
	private static final double SPEED_FACTOR = 0.25; 

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
		robot.getPilot().setTravelSpeed(SPEED_FACTOR*robot.getPilot().getMaxTravelSpeed());
	}
}
