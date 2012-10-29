package mazestormer.barcode;

import mazestormer.robot.Robot;

public class WaitAction implements IAction{

	@Override
	public void performAction(Robot robot) throws IllegalStateException {
		if(robot == null)
			throw new IllegalStateException("The given robot must be a valid robot.");
		//System.Threading.Thread.Sleep(2000);
	}

}
