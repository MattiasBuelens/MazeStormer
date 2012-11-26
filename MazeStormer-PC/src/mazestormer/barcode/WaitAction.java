package mazestormer.barcode;

import lejos.util.Delay;
import mazestormer.robot.Robot;

public class WaitAction implements IAction {

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if (robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");

		/*
		 * TODO This won't actually prevent the robot from doing anything. Other
		 * threads can still access and operate the robot while this thread is
		 * sleeping.
		 */
		robot.getPilot().stop();
		Delay.msDelay(5000);
	}
}
